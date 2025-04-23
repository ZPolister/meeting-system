package cn.polister.dianmeetingsystem.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录请求参数")
public class LoginDto {
    @Schema(description = "登录标识（用户名或邮箱）", example = "health_user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "登录标识不能为空")
    private String identifier;

    @Schema(description = "登录密码", example = "securePwd123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;
}