package cn.polister.dianmeetingsystem.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息数据传输对象（管理员接口）")
public class UserInfoAdminDto {
    private Long id;
    private String username;

    private String password;

    private String nickName;

    private String roleName;

    private String company;

    private String phone;

    private String email;
}
