package cn.polister.dianmeetingsystem.entity.vo;

import lombok.Data;

@Data
public class UserInfoVo {
    private Long id;

    private String username;

    private String nickName;

    private String roleName;

    private String company;

    private String phone;

    private String email;

    private Double balance;

    private String statusType;
}
