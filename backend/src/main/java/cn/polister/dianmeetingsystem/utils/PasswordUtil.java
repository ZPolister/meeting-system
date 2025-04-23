package cn.polister.dianmeetingsystem.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    /**
     * 生成加密密码
     * @param rawPassword 原始密码
     * @return 加密后的哈希值
     */
    public static String encode(CharSequence rawPassword) {
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
    }

    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的哈希值
     * @return 是否匹配
     */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }
}