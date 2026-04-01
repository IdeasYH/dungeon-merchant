package com.dungeon.merchant.module.auth.security;

import com.dungeon.merchant.config.JwtProperties;
import com.dungeon.merchant.module.auth.entity.AccountEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final Key signingKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(AccountEntity account) {
        return createToken(account, TokenType.ACCESS, jwtProperties.getExpiration());
    }

    public String createRefreshToken(AccountEntity account) {
        return createToken(account, TokenType.REFRESH, jwtProperties.getRefreshExpiration());
    }

    public Claims validateToken(String token, TokenType expectedTokenType) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token must not be blank");
        }
        try {
            Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            String tokenType = claims.get("tokenType", String.class);
            if (!expectedTokenType.name().equals(tokenType)) {
                throw new IllegalArgumentException("Invalid token type");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException ex) {
            if (ex instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            }
            throw new IllegalArgumentException("Invalid token", ex);
        }
    }

    public AuthUserPrincipal toPrincipal(Claims claims) {
        Number accountId = claims.get("accountId", Number.class);
        if (accountId == null) {
            throw new IllegalArgumentException("Token missing accountId");
        }
        return new AuthUserPrincipal(accountId.longValue(), claims.getSubject());
    }

    public long getAccessExpirationSeconds() {
        return jwtProperties.getExpiration() / 1000;
    }

    public long getRefreshExpirationSeconds() {
        return jwtProperties.getRefreshExpiration() / 1000;
    }

    private String createToken(AccountEntity account, TokenType tokenType, long expirationMillis) {
        if (account == null || account.getId() == null || !StringUtils.hasText(account.getUsername())) {
            throw new IllegalArgumentException("Account must contain id and username");
        }
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expirationMillis);
        return Jwts.builder()
            .subject(account.getUsername())
            .claim("accountId", account.getId())
            .claim("tokenType", tokenType.name())
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(signingKey)
            .compact();
    }
}
