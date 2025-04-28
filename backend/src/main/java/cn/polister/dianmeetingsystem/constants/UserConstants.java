package cn.polister.dianmeetingsystem.constants;

public class UserConstants {

    private UserConstants() {}

    /**
     * 用户状态：正常
     */
    public static final String USER_STATUS_NORMAL = "正常";

    /**
     * 用户状态：待审核
     */
    public static final String USER_STATUS_WAIT_AUDIT = "待审核";

    /**
     * 用户状态：冻结
     */
    public static final String USER_STATUS_FREEZE = "冻结";

    /**
     * 用户状态：审核不通过
     */
    public static final String USER_STATUS_AUDIT_FAIL = "审核不通过";


    /**
     * 用户角色：用户
     */
    public static final String USER_ROLE_NORMAL = "user";

    /**
     * 用户角色：员工
     */
    public static final String USER_ROLE_WORKER = "worker";

    /**
     * 用户角色：管理员
     */
    public static final String USER_ROLE_ADMIN = "admin";

    public static final String USER_REDIS_LOCK_KEY = "user:lock:";
}
