package cn.polister.dianmeetingsystem.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (RoomTimeSlot)表实体类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("room_time_slot")
public class RoomTimeSlot  {
@TableId
    private Long id;


    private Long roomId;

    private Date timeSlot;

    private Integer statusType;

    private Integer delFlag;


}
