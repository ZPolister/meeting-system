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
 * (Account)表实体类
 *
 * @author Polister
 * @since 2025-04-22 19:40:24
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("account")
public class Account  {
@TableId
    private Long id;


    private String username;

    private String password;

    private String email;

    private String nickName;

    private String roleName;

    private String company;

    private String phone;

    private BigDecimal balance;

    private String statusType;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;


}
