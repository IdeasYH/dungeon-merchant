package com.dungeon.merchant.module.common.exception;

public class BusinessException extends com.dungeon.merchant.exception.BusinessException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus(), message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
