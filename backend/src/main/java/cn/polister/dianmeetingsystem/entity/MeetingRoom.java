package cn.polister.dianmeetingsystem.entity;

import java.math.BigDecimal;
import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (MeetingRoom)表实体类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("meeting_room")
public class MeetingRoom  {
@TableId
    private Long id;


    private String roomName;

    private String roomType;

    private Integer capacity;

    private Integer hasProjector;

    private BigDecimal pricePerHour;

    private String roomStatus;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;


}
