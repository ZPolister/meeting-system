package cn.polister.dianmeetingsystem.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationAuditDto {
    private Long appId;
    private String reason;
}
