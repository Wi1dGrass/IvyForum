package com.ivy.forum.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(ErrorCode ec) {
        super(ec.getMessage());
        this.code = ec.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}