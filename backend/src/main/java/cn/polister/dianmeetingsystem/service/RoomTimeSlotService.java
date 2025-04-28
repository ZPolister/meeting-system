package cn.polister.dianmeetingsystem.service;

import cn.polister.dianmeetingsystem.entity.RoomTimeSlot;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;


/**
 * (RoomTimeSlot)表服务接口
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
public interface RoomTimeSlotService extends IService<RoomTimeSlot> {

    void removeByRoomId(Long id);

    Boolean checkTimeSlotFree(Long roomId, Date startTime, Date endTime);

    void updateTimeSlotStatus(Long roomId, Date startTime, Date endTime, String statusType);
}
