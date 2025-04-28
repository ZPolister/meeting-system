package cn.polister.dianmeetingsystem.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomDto {
    private String roomName;

    private String roomType;

    private Integer capacity;

    private Integer hasProjector;

    private BigDecimal pricePerHour;

    private String roomStatus;
}
