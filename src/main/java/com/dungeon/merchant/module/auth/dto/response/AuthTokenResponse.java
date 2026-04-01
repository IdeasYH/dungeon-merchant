package com.dungeon.merchant.module.auth.dto.response;

public record AuthTokenResponse(
    Long accountId,
    String username,
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    long refreshExpiresIn
) {
}
