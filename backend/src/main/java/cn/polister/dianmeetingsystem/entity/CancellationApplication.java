package cn.polister.dianmeetingsystem.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (CancellationApplication)表实体类
 *
 * @author Polister
 * @since 2025-04-22 19:40:30
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("cancellation_application")
public class CancellationApplication  {
@TableId
    private Long id;


    private Long orderId;

    private Date applyTime;

    private Integer refundPercent;

    private Long staffId;

    private String auditStatus;

    private String note;

    private Date auditTime;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;


}
