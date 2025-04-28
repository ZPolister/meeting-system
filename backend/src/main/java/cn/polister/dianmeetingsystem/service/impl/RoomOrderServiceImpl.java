package cn.polister.dianmeetingsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.polister.dianmeetingsystem.constants.CancelApplicationConstants;
import cn.polister.dianmeetingsystem.constants.MeetingRoomConstants;
import cn.polister.dianmeetingsystem.constants.OrderConstants;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.CancellationApplication;
import cn.polister.dianmeetingsystem.entity.MeetingRoom;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.RoomOrder;
import cn.polister.dianmeetingsystem.entity.dto.CancelOrderDto;
import cn.polister.dianmeetingsystem.entity.dto.RoomOrderDto;
import cn.polister.dianmeetingsystem.entity.dto.RoomRecommendDto;
import cn.polister.dianmeetingsystem.entity.vo.RoomRecommendVo;
import cn.polister.dianmeetingsystem.enums.AppHttpCodeEnum;
import cn.polister.dianmeetingsystem.exception.SystemException;
import cn.polister.dianmeetingsystem.mapper.MeetingRoomMapper;
import cn.polister.dianmeetingsystem.mapper.RoomOrderMapper;
import cn.polister.dianmeetingsystem.service.AccountService;
import cn.polister.dianmeetingsystem.service.CancellationApplicationService;
import cn.polister.dianmeetingsystem.service.RoomOrderService;
import cn.polister.dianmeetingsystem.service.RoomTimeSlotService;
import cn.polister.dianmeetingsystem.utils.RedissonLock;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * (RoomOrder)表服务实现类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@Service("roomOrderService")
public class RoomOrderServiceImpl extends ServiceImpl<RoomOrderMapper, RoomOrder> implements RoomOrderService {

    @Resource
    private MeetingRoomMapper meetingRoomMapper;

    @Resource
    private RoomTimeSlotService roomTimeSlotService;

    @Resource
    private AccountService accountService;

    @Resource
    private CancellationApplicationService cancellationApplicationService;

    @Resource
    private RedissonLock redissonLock;

    @Override
    public ResponseResult<List<RoomRecommendVo>> recommendRooms(RoomRecommendDto dto) {

        // 先根据基本信息查询会议室
        LambdaQueryWrapper<MeetingRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(dto.getRoomType()),
                        MeetingRoom::getRoomType, dto.getRoomType())
                    .ge(Objects.nonNull(dto.getCapacity()),
                        MeetingRoom::getCapacity, dto.getCapacity())
                    .eq(Objects.nonNull(dto.getHasProjector()),
                            MeetingRoom::getHasProjector, dto.getHasProjector())
                    .eq(Objects.nonNull(dto.getHasSound()),
                            MeetingRoom::getHasSound, dto.getHasSound())
                    .eq(Objects.nonNull(dto.getHasNetwork()),
                            MeetingRoom::getHasNetwork, dto.getHasNetwork())
                    .like(StringUtils.hasText(dto.getRoomName()),
                            MeetingRoom::getRoomName, dto.getRoomName());

        List<MeetingRoom> rooms = meetingRoomMapper.selectList(queryWrapper);

        // 再根据时间段查询会议室的订单
        List<RoomRecommendVo> roomRecommendVos = rooms.stream()
                .filter(room -> roomTimeSlotService.checkTimeSlotFree(room.getId(),
                                                    dto.getStartTime(), dto.getEndTime())
        ).map(room -> BeanUtil.copyProperties(room, RoomRecommendVo.class).setTotalPrice(
               this.calculateTotalPrice(room, dto.getStartTime(), dto.getEndTime())
        )).toList();

        return ResponseResult.okResult(roomRecommendVos);
    }

    @Override
    @Transactional
    public Long createOrder(RoomOrderDto orderDto, Long userId) {

        if (!redissonLock.lock(OrderConstants.ROOM_REDIS_LOCK_KEY + orderDto.getRoomId(),
                OrderConstants.ORDER_REDIS_KEY_EXPIRED)) {
            throw new SystemException(AppHttpCodeEnum.ORDER_CREATE_FAILED);
        }

        try {
            RoomOrder roomOrder = BeanUtil.copyProperties(orderDto, RoomOrder.class);
            roomOrder.setUserId(userId);

            if (!roomTimeSlotService.checkTimeSlotFree(roomOrder.getRoomId(),
                    roomOrder.getStartTime(), roomOrder.getEndTime())) {
                throw new SystemException(AppHttpCodeEnum.ORDER_TIME_SLOT_NOT_FREE);
            }

            MeetingRoom meetingRoom = meetingRoomMapper.selectById(orderDto.getRoomId());
            roomOrder.setTotalPrice(this.calculateTotalPrice(meetingRoom,
                    orderDto.getStartTime(), orderDto.getEndTime()));

            roomOrder.setOrderStatus(OrderConstants.ORDER_STATUS_WAIT_PAY);

            roomTimeSlotService.updateTimeSlotStatus(roomOrder.getRoomId(),
                    roomOrder.getStartTime(), roomOrder.getEndTime(),
                    MeetingRoomConstants.ROOM_STATUS_LOCK);
            this.save(roomOrder);

            return roomOrder.getId();
        } finally {
            redissonLock.unlock(OrderConstants.ROOM_REDIS_LOCK_KEY + orderDto.getRoomId());
        }
    }

    @Override
    public Page<RoomOrder> listOrders(Long userId, Integer pageNum, Integer pageSize, String status) {

        LambdaQueryWrapper<RoomOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoomOrder::getUserId, userId)
                .eq(StringUtils.hasText(status), RoomOrder::getOrderStatus, status)
                .orderByDesc(RoomOrder::getCreateTime);
        Page<RoomOrder> page = new Page<>(pageNum, pageSize);
        this.page(page, queryWrapper);
        return page;

    }

    @Override
    @Transactional
    public void payOrder(Long orderId, Long userId) {
        // 获取订单锁
        if (!redissonLock.lock(OrderConstants.ORDER_REDIS_LOCK_KEY + orderId,
                OrderConstants.ORDER_REDIS_KEY_EXPIRED)) {
            throw new SystemException(AppHttpCodeEnum.ORDER_PAY_FAILED);
        }
        // 获取账户余额锁
        if (!redissonLock.lock(UserConstants.USER_REDIS_LOCK_KEY + userId,
                OrderConstants.ORDER_REDIS_KEY_EXPIRED)) {
            redissonLock.unlock(OrderConstants.ORDER_REDIS_LOCK_KEY + orderId);
            throw new SystemException(AppHttpCodeEnum.ORDER_PAY_FAILED);
        }

        try {
            RoomOrder roomOrder = this.getById(orderId);
            if (Objects.isNull(roomOrder)) {
                throw new SystemException(AppHttpCodeEnum.ORDER_NOT_EXIST);
            }
            if (!Objects.equals(roomOrder.getUserId(), userId)) {
                throw new SystemException(AppHttpCodeEnum.NO_OPERATOR_AUTH);
            }
            if (OrderConstants.ORDER_STATUS_EXPIRED.equals(roomOrder.getOrderStatus())) {
                throw new SystemException(AppHttpCodeEnum.ORDER_EXPIRED);
            }
            if (OrderConstants.ORDER_STATUS_PAYED.equals(roomOrder.getOrderStatus())) {
                throw new SystemException(AppHttpCodeEnum.ORDER_PAYED);
            }

            accountService.payOrder(roomOrder.getTotalPrice(), userId);
            roomOrder.setOrderStatus(OrderConstants.ORDER_STATUS_PAYED);
            this.updateById(roomOrder);
            roomTimeSlotService.updateTimeSlotStatus(roomOrder.getRoomId(),
                    roomOrder.getStartTime(), roomOrder.getEndTime(),
                    MeetingRoomConstants.ROOM_STATUS_RESERVED);

        } finally {
            redissonLock.unlock(OrderConstants.ORDER_REDIS_LOCK_KEY + orderId);
            redissonLock.unlock(UserConstants.USER_REDIS_LOCK_KEY + userId);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(CancelOrderDto dto, Long userId) {
        if (!redissonLock.lock(OrderConstants.ORDER_REDIS_LOCK_KEY + dto.getOrderId(),
                OrderConstants.ORDER_REDIS_KEY_EXPIRED)) {
            throw new SystemException(AppHttpCodeEnum.ORDER_CANCEL_FAILED);
        }

        try {
            RoomOrder roomOrder = this.getById(dto.getOrderId());
            if (Objects.isNull(roomOrder)) {
                throw new SystemException(AppHttpCodeEnum.ORDER_NOT_EXIST);
            }
            if (!Objects.equals(roomOrder.getUserId(), userId)) {
                throw new SystemException(AppHttpCodeEnum.NO_OPERATOR_AUTH);
            }
            if (OrderConstants.ORDER_STATUS_CANCELING.equals(roomOrder.getOrderStatus())) {
                throw new SystemException(AppHttpCodeEnum.ORDER_CANCELING);
            }
            if (OrderConstants.ORDER_STATUS_CANCELED.equals(roomOrder.getOrderStatus())) {
                throw new SystemException(AppHttpCodeEnum.ORDER_CANCELED);
            }
            if (OrderConstants.ORDER_STATUS_EXPIRED.equals(roomOrder.getOrderStatus())) {
                throw new SystemException(AppHttpCodeEnum.ORDER_EXPIRED);
            }

            BigDecimal refundPercent = this.calculateRefundNumber(roomOrder.getStartTime());
            if (BigDecimal.ZERO.equals(refundPercent)) {
                throw new SystemException(AppHttpCodeEnum.ORDER_CANCEL_EXPIRED);
            }

            // 没付钱，直接取消
            if (OrderConstants.ORDER_STATUS_WAIT_PAY.equals(roomOrder.getOrderStatus())) {
                roomTimeSlotService.updateTimeSlotStatus(roomOrder.getRoomId(),
                        roomOrder.getStartTime(), roomOrder.getEndTime(),
                        MeetingRoomConstants.ROOM_STATUS_FREE);
                roomOrder.setOrderStatus(OrderConstants.ORDER_STATUS_CANCELED);
                this.updateById(roomOrder);
                return;
            }

            // 找到最早的一个退款申请
            LambdaQueryWrapper<CancellationApplication> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CancellationApplication::getOrderId, dto.getOrderId())
                   .orderByAsc(CancellationApplication::getCreateTime).last("limit 1");

            CancellationApplication cancellationApplication = new CancellationApplication();
            cancellationApplication.setOrderId(dto.getOrderId());
            cancellationApplication.setAuditReason(dto.getReason());
            cancellationApplication.setAuditStatus(CancelApplicationConstants.APPLICATION_STATUS_PENDING);

            CancellationApplication application = cancellationApplicationService.getOne(wrapper);
            if (Objects.isNull(application)) {
                cancellationApplication.setRefundPercent(refundPercent);
                roomOrder.setRefundAmount(Objects.requireNonNull(refundPercent).multiply(roomOrder.getTotalPrice()));
            } else {
                cancellationApplication.setRefundPercent(application.getRefundPercent());
            }

            cancellationApplicationService.save(cancellationApplication);
            roomOrder.setOrderStatus(OrderConstants.ORDER_STATUS_CANCELING);
            this.updateById(roomOrder);
        } finally {
            redissonLock.unlock(OrderConstants.ORDER_REDIS_LOCK_KEY + dto.getOrderId());
        }
    }

    @Override
    public void cancelCancelOrder(Long orderId, Long userId) {

        RoomOrder roomOrder = this.getById(orderId);
        if (Objects.isNull(roomOrder)) {
            throw new SystemException(AppHttpCodeEnum.ORDER_NOT_EXIST);
        }
        if (!Objects.equals(roomOrder.getUserId(), userId)) {
            throw new SystemException(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        if (!OrderConstants.ORDER_STATUS_CANCELING.equals(roomOrder.getOrderStatus())) {
            throw new SystemException(AppHttpCodeEnum.APPLICATION_CANCEL_FAILED);
        }

        if (!redissonLock.lock(OrderConstants.ORDER_REDIS_LOCK_KEY + orderId,
                OrderConstants.ORDER_REDIS_KEY_EXPIRED)) {
            throw new SystemException(AppHttpCodeEnum.APPLICATION_CANCEL_FAILED);
        }

        try {
            LambdaQueryWrapper<CancellationApplication> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CancellationApplication::getOrderId, orderId)
                    .eq(CancellationApplication::getAuditStatus, CancelApplicationConstants.APPLICATION_STATUS_PENDING);
            cancellationApplicationService.remove(wrapper);
            roomOrder.setOrderStatus(OrderConstants.ORDER_STATUS_PAYED);
            this.updateById(roomOrder);
        } finally {
            redissonLock.unlock(OrderConstants.ORDER_REDIS_LOCK_KEY + orderId);
        }
    }

    private BigDecimal calculateTotalPrice(MeetingRoom room, Date startTime, Date endTime) {
        return room.getPricePerHour().multiply(new BigDecimal(
                Duration.between(
                        startTime.toInstant(),
                        endTime.toInstant()
                ).toHours()
        ));
    }

    private BigDecimal calculateRefundNumber(Date startTime) {
        Date now = new Date();
        long hours = Duration.between(now.toInstant(), startTime.toInstant()).toHours();

        if (hours >= 72) {
            return new BigDecimal("1.00"); // 100%退款
        } else if (hours >= 48) {
            return new BigDecimal("0.75"); // 75%退款
        } else if (hours >= 24) {
            return new BigDecimal("0.25"); // 25%退款
        } else {
            return BigDecimal.ZERO; // 不退款
        }
    }

}
