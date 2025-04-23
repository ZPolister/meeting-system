package cn.polister.dianmeetingsystem.constants;

import java.time.Duration;

public class AuthConstants {

    /**
     * 请求间隔
     */
    public static final String REGISTER_CODE_INTERVAL_KEY = "register_code_interval:";
    public static final String RESET_CODE_INTERVAL_KEY = "reset_code_interval:";
    public static final Duration CODE_INTERVAL = Duration.ofSeconds(60);

    /**
     * 验证码过期时间
     */
    public static final Duration CODE_EXPIRE = Duration.ofMinutes(5);
    public static final String REGISTER_CODE_KEY = "register_code:";
    public static final String RESET_CODE_KEY = "reset_code:";
}
