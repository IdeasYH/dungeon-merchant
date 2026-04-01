package com.dungeon.merchant.module.auth.security;

import com.dungeon.merchant.config.JwtProperties;
import com.dungeon.merchant.module.auth.entity.AccountEntity;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    @Test
    void createAndValidateAccessToken() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-that-is-at-least-32-bytes-long");
        properties.setExpiration(600000L);
        properties.setRefreshExpiration(3600000L);

        JwtTokenProvider provider = new JwtTokenProvider(properties);
        AccountEntity account = new AccountEntity();
        account.setId(42L);
        account.setUsername("merchant");

        String token = provider.createAccessToken(account);
        Claims claims = provider.validateToken(token, TokenType.ACCESS);

        assertThat(claims.getSubject()).isEqualTo("merchant");
        assertThat(claims.get("accountId", Number.class).longValue()).isEqualTo(42L);
        assertThat(claims.get("tokenType", String.class)).isEqualTo(TokenType.ACCESS.name());
    }

    @Test
    void refreshValidationRejectsAccessTokens() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-that-is-at-least-32-bytes-long");
        properties.setExpiration(600000L);
        properties.setRefreshExpiration(3600000L);

        JwtTokenProvider provider = new JwtTokenProvider(properties);
        AccountEntity account = new AccountEntity();
        account.setId(7L);
        account.setUsername("blacksmith");

        String accessToken = provider.createAccessToken(account);

        assertThatThrownBy(() -> provider.validateToken(accessToken, TokenType.REFRESH))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid token type");
    }
}
