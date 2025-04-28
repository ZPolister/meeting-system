package cn.polister.dianmeetingsystem.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRecommendDto {
    private String roomName;

    private String roomType;

    private Integer capacity;

    private Boolean hasProjector;

    private Boolean hasSound;

    private Boolean hasNetwork;

    private Date startTime;

    private Date endTime;
}
