package cn.polister.dianmeetingsystem.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息数据传输对象")
public class UserInfoDto {
    private String nickName;
    private String company;
    private String phone;
}
