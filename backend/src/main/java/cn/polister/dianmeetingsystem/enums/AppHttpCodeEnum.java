package cn.polister.dianmeetingsystem.enums;


import lombok.Getter;

/**
 * 返回码枚举类
 */
@Getter
public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    USERNAME_EXIST(501,"用户名已存在"),
    PHONE_NUMBER_EXIST(502,"手机号已存在"),
    EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    LOGIN_ERROR(505,"用户名或密码错误"),
    FILE_TYPE_ERROR(507,"文件类型错误"),
    NICKNAME_EXIST(508, "别名已存在"),
    USERNAME_NOT_NULL(509, "用户名不能为空"),
    PASSWORD_NOT_NULL(509, "密码不能为空"),
    EMAIL_NOT_NULL(509, "邮件不能为空"),
    NICKNAME_NOT_NULL(509,  "别名不能为空"),
    EMAIL_CODE_ERROR(510,"验证码错误或已过期"),
    USER_BANNED(511, "用户已被禁用"),
    EMAIL_CODE_SEND(512, "验证码请求过于频繁"),
    DATA_NOT_EXIST(513, "数据不存在"),
    PARAMETER_INVALID(400, "参数校验错误"),
    USER_WAIT_AUDIT(514, "账户待审核"),
    USER_AUDIT_FAIL(515, "账户审核未通过"),
    USER_NOT_FOUND(405, "用户不存在"),
    USER_ALREADY_AUDIT(516, "账号已经审批"),
    USER_NOT_ALLOW_BAN(517, "不能封禁非正常状态的用户"),
    USER_NOT_ALLOW_RECOVER(518, "不能恢复非封禁状态用户"),
    ORDER_TIME_SLOT_NOT_FREE(519, "所选会议室已被预定"),
    ORDER_NOT_EXIST(406, "订单不存在"),
    ORDER_EXPIRED(521, "订单已过期"),
    ORDER_PAYED(522,"不能重复支付订单"),
    ORDER_CREATE_FAILED(523,  "创建订单失败"),
    ORDER_PAY_FAILED(524, "订单支付失败"),
    BALANCE_NOT_ENOUGH(525, "余额不足，请充值"),
    ORDER_CANCEL_FAILED(526, "申请取消订单失败"),
    ORDER_CANCELING(527, "订单正在取消，不能重复取消"),
    ORDER_CANCELED(528, "订单已经取消"),
    ORDER_CANCEL_EXPIRED(529, "订单已经超过退款期限"),
    APPLICATION_CANCEL_FAILED(530, "申请取消失败"),
    AUDIT_ORDER_FAIL(531, "审核申请失败"),
    RECHARGE_FAIL(532, "充值失败");


    final int code;
    final String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

}