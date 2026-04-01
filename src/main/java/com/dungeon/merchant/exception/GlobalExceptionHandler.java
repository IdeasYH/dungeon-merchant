package com.dungeon.merchant.exception;

import com.dungeon.merchant.module.common.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.error(exception.getStatus().value(), exception.getMessage());
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        ConstraintViolationException.class,
        MissingRequestValueException.class,
        IllegalArgumentException.class
    })
    public ApiResponse<Void> handleBadRequest(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            FieldError fieldError = methodArgumentNotValidException.getBindingResult().getFieldError();
            String message = fieldError == null ? "参数错误" : "参数错误: " + fieldError.getDefaultMessage();
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message);
        }
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "参数错误: " + exception.getMessage());
    }
}
