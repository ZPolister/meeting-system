package cn.polister.dianmeetingsystem.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomStaffVo {
    private Long id;
    private String roomName;
    private String roomType;
    private String roomStatus;
}
