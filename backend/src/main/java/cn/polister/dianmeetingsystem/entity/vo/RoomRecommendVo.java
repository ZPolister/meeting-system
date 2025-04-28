package cn.polister.dianmeetingsystem.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RoomRecommendVo {
    private Long id;

    private String roomName;

    private String roomType;

    private Integer capacity;

    private Boolean hasProjector;

    private Boolean hasSound;

    private Boolean hasNetwork;

    private BigDecimal pricePerHour;

    private String roomStatus;

    private BigDecimal totalPrice;
}
