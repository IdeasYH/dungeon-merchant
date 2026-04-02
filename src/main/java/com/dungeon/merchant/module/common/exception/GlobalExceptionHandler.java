package com.dungeon.merchant.module.common.exception;

import com.dungeon.merchant.module.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        HttpStatus status = ex.getErrorCode().getHttpStatus();
        return ResponseEntity.status(status)
            .body(ApiResponse.error(status.value(), ex.getMessage()));
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        BindException.class,
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数错误: " + extractMessage(ex)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
    }

    private String extractMessage(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException
            && methodArgumentNotValidException.getBindingResult().getFieldError() != null) {
            return methodArgumentNotValidException.getBindingResult().getFieldError().getDefaultMessage();
        }
        if (ex instanceof BindException bindException && bindException.getBindingResult().getFieldError() != null) {
            return bindException.getBindingResult().getFieldError().getDefaultMessage();
        }
        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "请求数据无效";
        }
        return ex.getMessage();
    }
}
