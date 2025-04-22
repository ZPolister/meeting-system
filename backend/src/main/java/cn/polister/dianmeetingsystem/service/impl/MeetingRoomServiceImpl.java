package cn.polister.dianmeetingsystem.service.impl;

import cn.polister.dianmeetingsystem.entity.MeetingRoom;
import cn.polister.dianmeetingsystem.mapper.MeetingRoomMapper;
import cn.polister.dianmeetingsystem.service.MeetingRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (MeetingRoom)表服务实现类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@Service("meetingRoomService")
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom> implements MeetingRoomService {

}
