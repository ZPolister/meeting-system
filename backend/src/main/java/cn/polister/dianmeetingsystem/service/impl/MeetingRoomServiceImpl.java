package cn.polister.dianmeetingsystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.polister.dianmeetingsystem.constants.MeetingRoomConstants;
import cn.polister.dianmeetingsystem.entity.Account;
import cn.polister.dianmeetingsystem.entity.MeetingRoom;
import cn.polister.dianmeetingsystem.entity.RoomOrder;
import cn.polister.dianmeetingsystem.entity.RoomTimeSlot;
import cn.polister.dianmeetingsystem.entity.dto.MeetingRoomDto;
import cn.polister.dianmeetingsystem.entity.vo.MeetingRoomVo;
import cn.polister.dianmeetingsystem.enums.AppHttpCodeEnum;
import cn.polister.dianmeetingsystem.exception.SystemException;
import cn.polister.dianmeetingsystem.mapper.MeetingRoomMapper;
import cn.polister.dianmeetingsystem.service.AccountService;
import cn.polister.dianmeetingsystem.service.MeetingRoomService;
import cn.polister.dianmeetingsystem.service.RoomOrderService;
import cn.polister.dianmeetingsystem.service.RoomTimeSlotService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * (MeetingRoom)表服务实现类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@Service("meetingRoomService")
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom> implements MeetingRoomService {

    @Resource
    private RoomTimeSlotService roomTimeSlotService;

    @Resource
    private RoomOrderService roomOrderService;

    @Resource
    private AccountService accountService;

    @Transactional
    public void saveWithTimeSlots(MeetingRoomDto meetingRoomDto) {

        MeetingRoom meetingRoom = BeanUtil.toBean(meetingRoomDto, MeetingRoom.class);
        // 保存会议室
        this.save(meetingRoom);

        // 生成时间粒度
        List<RoomTimeSlot> slots = generateTimeSlots(meetingRoom.getId());
        roomTimeSlotService.saveBatch(slots);
    }

    @Override
    public void deleteMeetingRoom(Long id) {
        // 删除会议室
        this.removeById(id);
        // 删除对应的时间槽
        roomTimeSlotService.removeByRoomId(id);
    }

    private List<RoomTimeSlot> generateTimeSlots(Long roomId) {
        List<RoomTimeSlot> slots = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        // 生成未来60天时间段
        for (int day = 0; day < 60; day++) {
            LocalDate date = now.plusDays(day).toLocalDate();

            // 每天8-21点
            for (int hour = 8; hour < 21; hour++) {
                LocalDateTime slotTime = date.atTime(hour, 0);
                slots.add(new RoomTimeSlot(roomId, slotTime));
            }
        }

        return slots;
    }

    public Page<MeetingRoomVo> getMeetingRoomsByPage(Integer pageNum, Integer pageSize,
                                                     String roomType, String roomStatus) {
        LambdaQueryWrapper<MeetingRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(roomType), MeetingRoom::getRoomType, roomType)
               .eq(StringUtils.hasText(roomStatus), MeetingRoom::getRoomStatus, roomStatus);

        Page<MeetingRoom> page = new Page<>(pageNum, pageSize);
        this.page(page, wrapper);

        // 转换VO并处理预订信息
        return (Page<MeetingRoomVo>) page.convert(meetingRoom -> {
            MeetingRoomVo vo = BeanUtil.copyProperties(meetingRoom, MeetingRoomVo.class);

            if ("预定".equals(roomStatus)) {
                List<RoomTimeSlot> slots = roomTimeSlotService.lambdaQuery()
                    .eq(RoomTimeSlot::getRoomId, meetingRoom.getId())
                    .eq(RoomTimeSlot::getStatusType, "预定")
                    .list();

                slots.forEach(slot -> {
                    RoomOrder order = roomOrderService.lambdaQuery()
                        .eq(RoomOrder::getRoomId, meetingRoom.getId())
                        .eq(RoomOrder::getStartTime, slot.getTimeSlot())
                        .one();

                    if (order != null) {
                        Account user = accountService.getById(order.getUserId());
                        vo.addReservationInfo(slot.getTimeSlot(), user.getUsername());
                    }
                });
            }

            return vo;
        });
    }

    @Override
    public void setMeetingRoomStatus(Long roomId, String status) {
        MeetingRoom meetingRoom = this.getById(roomId);
        if (Objects.isNull(meetingRoom)) {
            throw new SystemException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if (!MeetingRoomConstants.ROOM_STATUS_FREE.equals(status)
                && !MeetingRoomConstants.ROOM_STATUS_MAINTENANCE.equals(status)
                && !MeetingRoomConstants.ROOM_STATUS_USING.equals(status)) {
                    throw new SystemException(AppHttpCodeEnum.PARAMETER_INVALID);
                }
        meetingRoom.setRoomStatus(status);
        this.updateById(meetingRoom);
    }
}
