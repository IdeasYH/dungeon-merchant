package com.dungeon.merchant.module.common.response;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record ApiResponse<T>(
    int code,
    String message,
    T data,
    OffsetDateTime timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data, OffsetDateTime.now(ZoneOffset.UTC));
    }

    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(code, message, null, OffsetDateTime.now(ZoneOffset.UTC));
    }
}
