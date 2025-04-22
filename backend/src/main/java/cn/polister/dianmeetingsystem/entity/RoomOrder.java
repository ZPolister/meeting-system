package cn.polister.dianmeetingsystem.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (RoomOrder)表实体类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("room_order")
public class RoomOrder  {
@TableId
    private Long id;


    private Long userId;

    private Long roomId;

    private Date startTime;

    private Date endTime;

    private Double totalPrice;

    private String orderStatus;

    private Date createTime;

    private Date updateTime;

    private Date paymentTime;

    private Date cancelTime;

    private Double refundAmount;

    private Integer delFlag;


}
