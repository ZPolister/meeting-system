package cn.polister.dianmeetingsystem.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息数据传输对象")
public class UserInfoDto {
    @Schema(description = "生日（格式：yyyy-MM-dd）", example = "1990-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String birthday;
}
