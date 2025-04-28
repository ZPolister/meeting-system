package cn.polister.dianmeetingsystem.config;

import cn.polister.dianmeetingsystem.entity.MeetingRoom;
import cn.polister.dianmeetingsystem.entity.RoomTimeSlot;
import cn.polister.dianmeetingsystem.service.MeetingRoomService;
import cn.polister.dianmeetingsystem.service.RoomTimeSlotService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Resource
    private MeetingRoomService meetingRoomService;
    @Resource
    private RoomTimeSlotService roomTimeSlotService;

    // 每天23点执行
    @Scheduled(cron = "0 55 23 * * ?")
    public void generateFutureTimeSlots() {
        List<MeetingRoom> rooms = meetingRoomService.list();
        LocalDateTime start = LocalDateTime.now().plusDays(60).withHour(0).withMinute(0);

        rooms.forEach(room -> {
            List<RoomTimeSlot> slots = generateNextDaySlots(room.getId(), start);
            roomTimeSlotService.saveBatch(slots);
        });
    }

    private List<RoomTimeSlot> generateNextDaySlots(Long roomId, LocalDateTime baseTime) {
        List<RoomTimeSlot> slots = new ArrayList<>();
        LocalDate date = baseTime.toLocalDate();

        for (int hour = 8; hour < 21; hour++) {
            LocalDateTime slotTime = date.atTime(hour, 0);
            slots.add(new RoomTimeSlot(roomId, slotTime));
        }

        return slots;
    }
}
