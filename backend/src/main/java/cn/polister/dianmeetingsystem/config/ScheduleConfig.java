package cn.polister.dianmeetingsystem.config;

import cn.polister.dianmeetingsystem.constants.OrderConstants;
import cn.polister.dianmeetingsystem.entity.MeetingRoom;
import cn.polister.dianmeetingsystem.entity.RoomOrder;
import cn.polister.dianmeetingsystem.entity.RoomTimeSlot;
import cn.polister.dianmeetingsystem.entity.dto.CancelOrderDto;
import cn.polister.dianmeetingsystem.service.MeetingRoomService;
import cn.polister.dianmeetingsystem.service.RoomOrderService;
import cn.polister.dianmeetingsystem.service.RoomTimeSlotService;
import cn.polister.dianmeetingsystem.utils.RedissonLock;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {

    @Resource
    private MeetingRoomService meetingRoomService;
    @Resource
    private RoomTimeSlotService roomTimeSlotService;
    @Resource
    private RoomOrderService roomOrderService;
    @Resource
    private RedissonLock redissonLock;

    // 更新60天后的时间槽
    @Scheduled(cron = "0 50 23 * * ?")
    public void generateFutureTimeSlots() {
        List<MeetingRoom> rooms = meetingRoomService.list();
        LocalDateTime start = LocalDateTime.now().plusDays(60).withHour(0).withMinute(0);

        rooms.forEach(room -> {
            List<RoomTimeSlot> slots = generateNextDaySlots(room.getId(), start);
            roomTimeSlotService.saveBatch(slots);
        });
    }

    // 取消过期订单
    @Scheduled(fixedRate = 60 * 1000)
    public void checkAndDeleteExpiredOrder() {
        log.info("执行定时任务：检查并删除过期订单");
        if (!redissonLock.lock(OrderConstants.ORDER_REDIS_LOCK_KEY_SCHEDULED, 10000)) {
            return;
        }

        try {
//            log.info("获取到锁，执行任务");
            // 获取所有没支付的订单
            List<RoomOrder> orders = roomOrderService.list(new LambdaQueryWrapper<RoomOrder>()
                    .eq(RoomOrder::getOrderStatus, OrderConstants.ORDER_STATUS_WAIT_PAY));
//            log.info("查询到{}条订单", orders.size());

            orders.stream()
                    .filter(order -> order.getCreateTime().before(
                            Date.from(LocalDateTime.now()
                                    .minusMinutes(30)
                                    .atZone(ZoneOffset.systemDefault()).toInstant())))
                    .forEach(order -> {
                        log.info("订单{}已过期", order.getId());
                        roomOrderService.removeExpiredOrder(order.getId());
                    });
        } finally {
            redissonLock.unlock(OrderConstants.ORDER_REDIS_LOCK_KEY_SCHEDULED);
        }
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
