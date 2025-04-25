package cn.polister.dianmeetingsystem.service;

import cn.polister.dianmeetingsystem.entity.MeetingRoom;
import cn.polister.dianmeetingsystem.entity.dto.MeetingRoomDto;
import cn.polister.dianmeetingsystem.entity.vo.MeetingRoomVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * (MeetingRoom)表服务接口
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
public interface MeetingRoomService extends IService<MeetingRoom> {

    void saveWithTimeSlots(MeetingRoomDto meetingRoom);

    void deleteMeetingRoom(Long id);

    Page<MeetingRoomVo> getMeetingRoomsByPage(Integer pageNum, Integer pageSize, String roomType, String roomStatus);
}
