package cn.polister.dianmeetingsystem.constants;

import java.time.Duration;

public class AuthConstants {

    /**
     * 请求间隔
     */
    public static final String VERIFY_CODE_INTERVAL_KEY = "verify_code_interval:";
    public static final Duration CODE_INTERVAL = Duration.ofSeconds(60);

    /**
     * 验证码过期时间
     */
    public static final String VERIFY_CODE_KEY = "verify_code:";
    public static final Duration CODE_EXPIRE = Duration.ofMinutes(5);
}
