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
    PARAMETER_INVALID(400, "参数校验错误");


    final int code;
    final String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

}