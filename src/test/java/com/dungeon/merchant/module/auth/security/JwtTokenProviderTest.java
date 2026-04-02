package com.dungeon.merchant.module.auth.security;

import com.dungeon.merchant.config.JwtProperties;
import com.dungeon.merchant.module.auth.entity.AccountEntity;
import io.jsonwebtoken.Claims;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThatCode;
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

    @Test
    void defaultApplicationSecretIsStrongEnoughForProviderStartup() {
        String defaultSecret = extractDefaultJwtSecret();
        JwtProperties properties = new JwtProperties();
        properties.setSecret(defaultSecret);
        properties.setExpiration(600000L);
        properties.setRefreshExpiration(3600000L);

        assertThat(defaultSecret.length()).isGreaterThanOrEqualTo(32);
        assertThatCode(() -> new JwtTokenProvider(properties))
            .doesNotThrowAnyException();
    }

    private String extractDefaultJwtSecret() {
        Properties properties = loadApplicationProperties();
        String configuredSecret = properties.getProperty("jwt.secret");
        int defaultSeparatorIndex = configuredSecret.indexOf(':');
        int closingBraceIndex = configuredSecret.lastIndexOf('}');
        return configuredSecret.substring(defaultSeparatorIndex + 1, closingBraceIndex);
    }

    private Properties loadApplicationProperties() {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource("application.yml"));
        Properties properties = factoryBean.getObject();
        if (properties == null) {
            throw new IllegalStateException("Failed to load application.yml");
        }
        return properties;
    }
}
