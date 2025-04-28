package cn.polister.dianmeetingsystem.entity.vo;

import cn.polister.dianmeetingsystem.entity.MeetingRoom;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MeetingRoomVo extends MeetingRoom {
    // 预订信息列表
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ReservationInfo> reservations;

    public void addReservationInfo(Date time, String booker) {
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        reservations.add(new ReservationInfo(time, booker));
    }

    @Data
    @AllArgsConstructor
    public static class ReservationInfo {
        private Date reservedTime;
        private String bookerName;
    }
}
