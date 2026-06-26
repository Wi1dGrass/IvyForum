package com.ivy.forum.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 1xxx 认证 / 授权
    UNAUTHORIZED(1001, "未登录或登录过期"),
    TOKEN_INVALID(1002, "Token 无效"),
    BAD_CREDENTIALS(1003, "用户名或密码错误"),
    FORBIDDEN(1004, "权限不足"),
    USER_DISABLED(1005, "用户已被封禁"),
    USERNAME_EXISTS(1006, "用户名已存在"),

    // 2xxx 参数校验
    PARAM_INVALID(2001, "参数校验失败"),
    PARAM_MISSING(2002, "参数缺失"),

    // 3xxx 业务
    NOT_FOUND(3001, "资源不存在"),
    BIZ_ERROR(3002, "业务异常"),
    CHANNEL_DISABLED(3003, "频道已被禁用"),
    TAG_LIMIT_EXCEEDED(3004, "标签数量超出限制(最多5个)"),
    DUPLICATE_LIKE(3005, "已点赞"),
    REPORT_HANDLED(3006, "举报已处理"),

    // 5xxx 服务端
    SERVER_ERROR(5001, "服务器内部错误");

    private final Integer code;
    private final String message;
}