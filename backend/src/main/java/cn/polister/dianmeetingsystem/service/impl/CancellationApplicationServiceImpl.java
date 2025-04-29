package cn.polister.dianmeetingsystem.service.impl;

import cn.polister.dianmeetingsystem.constants.CancelApplicationConstants;
import cn.polister.dianmeetingsystem.constants.MeetingRoomConstants;
import cn.polister.dianmeetingsystem.constants.OrderConstants;
import cn.polister.dianmeetingsystem.constants.UserConstants;
import cn.polister.dianmeetingsystem.entity.CancellationApplication;
import cn.polister.dianmeetingsystem.entity.ResponseResult;
import cn.polister.dianmeetingsystem.entity.RoomOrder;
import cn.polister.dianmeetingsystem.entity.dto.ApplicationAuditDto;
import cn.polister.dianmeetingsystem.enums.AppHttpCodeEnum;
import cn.polister.dianmeetingsystem.exception.SystemException;
import cn.polister.dianmeetingsystem.mapper.CancellationApplicationMapper;
import cn.polister.dianmeetingsystem.mapper.RoomOrderMapper;
import cn.polister.dianmeetingsystem.service.AccountService;
import cn.polister.dianmeetingsystem.service.CancellationApplicationService;
import cn.polister.dianmeetingsystem.service.RoomTimeSlotService;
import cn.polister.dianmeetingsystem.utils.RedissonLock;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * (CancellationApplication)表服务实现类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@Service("cancellationApplicationService")
public class CancellationApplicationServiceImpl extends ServiceImpl<CancellationApplicationMapper, CancellationApplication> implements CancellationApplicationService {

    @Resource
    private RedissonLock redissonLock;

    @Resource
    private RoomOrderMapper roomOrderMapper;

    @Resource
    private AccountService accountService;

    @Resource
    private RoomTimeSlotService roomTimeSlotService;


    @Override
    public ResponseResult<Page<CancellationApplication>> getCancelApplicationList(Integer pageNum, Integer pageSize, String status) {
        Page<CancellationApplication> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CancellationApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(status), CancellationApplication::getAuditStatus, status)
                .orderByDesc(CancellationApplication::getCreateTime);

        page(page, queryWrapper);
        return ResponseResult.okResult(page);
    }

    @Override
    @Transactional
    public ResponseResult<Boolean> approveCancelApplication(Long userId, ApplicationAuditDto dto) {
        CancellationApplication cancellationApplication = this.getById(dto.getAppId());
        if (Objects.isNull(cancellationApplication)) {
            throw new SystemException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        RoomOrder roomOrder = roomOrderMapper.selectById(cancellationApplication.getOrderId());
        if (Objects.isNull(roomOrder)) {
            throw new SystemException(AppHttpCodeEnum.ORDER_NOT_EXIST);
        }

        if (!redissonLock.lock(OrderConstants.ORDER_REDIS_LOCK_KEY + cancellationApplication.getOrderId(),
                10000L)){
            throw new SystemException(AppHttpCodeEnum.AUDIT_ORDER_FAIL);
        }
        if (!redissonLock.lock(UserConstants.USER_REDIS_LOCK_KEY + roomOrder.getUserId(), 10000L)) {
            redissonLock.unlock(OrderConstants.ORDER_REDIS_LOCK_KEY + cancellationApplication.getOrderId());
            throw new SystemException(AppHttpCodeEnum.AUDIT_ORDER_FAIL);
        }

        try {
            cancellationApplication.setAuditStatus(CancelApplicationConstants.APPLICATION_STATUS_ACCEPTED);
            cancellationApplication.setNote(dto.getReason());
            cancellationApplication.setStaffId(userId);
            cancellationApplication.setAuditTime(new Date());

            // 退钱
            accountService.refundOrder(roomOrder.getTotalPrice(), roomOrder.getUserId());

            // 更新订单状态
            roomTimeSlotService.updateTimeSlotStatus(roomOrder.getRoomId(),
                    roomOrder.getStartTime(), roomOrder.getEndTime(),
                    MeetingRoomConstants.ROOM_STATUS_FREE);
            roomOrder.setOrderStatus(OrderConstants.ORDER_STATUS_CANCELED);
            roomOrder.setCancelTime(new Date());
            roomOrderMapper.updateById(roomOrder);


            return ResponseResult.okResult(this.updateById(cancellationApplication));
        } finally {
            redissonLock.unlock(OrderConstants.ORDER_REDIS_LOCK_KEY + cancellationApplication.getOrderId());
            redissonLock.unlock(UserConstants.USER_REDIS_LOCK_KEY + roomOrder.getUserId());
        }
    }

    @Override
    @Transactional
    public ResponseResult<Boolean> rejectCancelApplication(Long id, ApplicationAuditDto dto) {
        CancellationApplication cancellationApplication = this.getById(dto.getAppId());
        if (Objects.isNull(cancellationApplication)) {
            throw new SystemException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        RoomOrder roomOrder = roomOrderMapper.selectById(cancellationApplication.getOrderId());
        if (Objects.isNull(roomOrder)) {
            throw new SystemException(AppHttpCodeEnum.ORDER_NOT_EXIST);
        }

        if (!redissonLock.lock(OrderConstants.ORDER_REDIS_LOCK_KEY + cancellationApplication.getOrderId(),
                10000L)){
            throw new SystemException(AppHttpCodeEnum.AUDIT_ORDER_FAIL);
        }

        try {
            cancellationApplication.setAuditStatus(CancelApplicationConstants.APPLICATION_STATUS_REJECTED);
            cancellationApplication.setNote(dto.getReason());
            cancellationApplication.setStaffId(id);
            cancellationApplication.setAuditTime(new Date());

            // 更新订单状态
            roomOrder.setOrderStatus(OrderConstants.ORDER_STATUS_REJECT_CANCEL);
            roomOrderMapper.updateById(roomOrder);

            return ResponseResult.okResult(this.updateById(cancellationApplication));
        } finally {
            redissonLock.unlock(OrderConstants.ORDER_REDIS_LOCK_KEY + cancellationApplication.getOrderId());
        }
    }
}
