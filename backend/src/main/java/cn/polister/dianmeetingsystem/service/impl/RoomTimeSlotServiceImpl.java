package cn.polister.dianmeetingsystem.service.impl;

import cn.polister.dianmeetingsystem.constants.MeetingRoomConstants;
import cn.polister.dianmeetingsystem.entity.RoomTimeSlot;
import cn.polister.dianmeetingsystem.mapper.RoomTimeSlotMapper;
import cn.polister.dianmeetingsystem.service.RoomTimeSlotService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * (RoomTimeSlot)表服务实现类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@Service("roomTimeSlotService")
public class RoomTimeSlotServiceImpl extends ServiceImpl<RoomTimeSlotMapper, RoomTimeSlot> implements RoomTimeSlotService {

    @Resource
    private RoomTimeSlotMapper roomTimeSlotMapper;

    @Override
    public void removeByRoomId(Long roomId) {
        // 使用wrapper条件构造器删除指定roomId的记录
        LambdaQueryWrapper<RoomTimeSlot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomTimeSlot::getRoomId, roomId);
        this.remove(wrapper);
    }

    @Override
    public Boolean checkTimeSlotFree(Long roomId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RoomTimeSlot> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.eq(RoomTimeSlot::getRoomId, roomId)
                .ge(RoomTimeSlot::getTimeSlot, startTime)
                .lt(RoomTimeSlot::getTimeSlot, endTime)
                .orderByAsc(RoomTimeSlot::getTimeSlot);
        List<RoomTimeSlot> roomTimeSlots = roomTimeSlotMapper.selectList(orderQueryWrapper);
        for (RoomTimeSlot roomTimeSlot : roomTimeSlots) {
            if (!MeetingRoomConstants.ROOM_STATUS_FREE.equals(roomTimeSlot.getStatusType())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void updateTimeSlotStatus(Long roomId, Date startTime, Date endTime, String statusType) {
        LambdaQueryWrapper<RoomTimeSlot> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.eq(RoomTimeSlot::getRoomId, roomId)
                .ge(RoomTimeSlot::getTimeSlot, startTime)
               .lt(RoomTimeSlot::getTimeSlot, endTime)
               .orderByAsc(RoomTimeSlot::getTimeSlot);

        List<RoomTimeSlot> roomTimeSlots = roomTimeSlotMapper.selectList(orderQueryWrapper);
        for (RoomTimeSlot roomTimeSlot : roomTimeSlots) {
            roomTimeSlot.setStatusType(statusType);
        }
        roomTimeSlotMapper.updateById(roomTimeSlots);
    }
}
