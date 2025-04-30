package cn.polister.dianmeetingsystem.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundNumberVo {
    private BigDecimal refundPercent;
    private BigDecimal refundAmount;
}
