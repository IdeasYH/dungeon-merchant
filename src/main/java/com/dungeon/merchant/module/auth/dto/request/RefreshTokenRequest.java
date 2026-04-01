package com.dungeon.merchant.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "refreshToken 不能为空")
    String refreshToken
) {
}
