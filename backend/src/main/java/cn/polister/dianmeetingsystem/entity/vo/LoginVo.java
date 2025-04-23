package cn.polister.dianmeetingsystem.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {
    private String token;
    private UserInfoVo userInfo;
}
