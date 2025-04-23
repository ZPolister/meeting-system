package cn.polister.dianmeetingsystem.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "注册请求参数")
public class RegisterDto {
    @Schema(description = "用户名", example = "user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "邮箱地址", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "密码（6-20位字符）", example = "securePwd123", minLength = 6, maxLength = 20, requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 6, max = 20, message = "密码必须6-20位")
    private String password;

    @Schema(description = "邮箱验证码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "验证码不能为空")
    private String code;

    @Schema(description = "显示用户名", example = "Dian_Bro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickName;

    @Schema(description = "所属公司", example = "华南农业大学", requiredMode = Schema.RequiredMode.REQUIRED)
    private String company;

    @Schema(description = "电话号码", example = "18888888888", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;
}