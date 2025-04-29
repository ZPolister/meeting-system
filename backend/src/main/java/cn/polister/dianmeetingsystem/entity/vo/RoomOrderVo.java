package cn.polister.dianmeetingsystem.entity.vo;

import cn.polister.dianmeetingsystem.entity.CancellationApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomOrderVo {
    private Long id;

    private Date startTime;

    private Date endTime;

    private BigDecimal totalPrice;

    private String orderStatus;

    private Date createTime;

    private Date updateTime;

    private Date paymentTime;

    private Date cancelTime;

    private BigDecimal refundAmount;

    private UserInfoVo userInfo;

    private MeetingRoomInfoVo meetingRoomInfo;

    private List<CancellationApplication> cancellationApplicationList;

}
