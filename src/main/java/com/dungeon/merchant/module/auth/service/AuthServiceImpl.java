package com.dungeon.merchant.module.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dungeon.merchant.module.auth.dto.request.LoginRequest;
import com.dungeon.merchant.module.auth.dto.request.RefreshTokenRequest;
import com.dungeon.merchant.module.auth.dto.request.RegisterRequest;
import com.dungeon.merchant.module.auth.dto.response.AuthTokenResponse;
import com.dungeon.merchant.module.auth.entity.AccountEntity;
import com.dungeon.merchant.module.auth.repository.AccountMapper;
import com.dungeon.merchant.module.auth.security.AuthUserPrincipal;
import com.dungeon.merchant.module.auth.security.JwtTokenProvider;
import com.dungeon.merchant.module.auth.security.TokenType;
import com.dungeon.merchant.module.common.exception.BusinessException;
import com.dungeon.merchant.module.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(
        AccountMapper accountMapper,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider
    ) {
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public AuthTokenResponse register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request 不能为空");
        }
        String username = normalizeUsername(request.username());
        if (findByUsername(username) != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "Username already exists");
        }

        AccountEntity account = new AccountEntity();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        accountMapper.insert(account);
        return buildAuthResponse(account);
    }

    @Override
    public AuthTokenResponse login(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request 不能为空");
        }
        String username = normalizeUsername(request.username());
        AccountEntity account = findByUsername(username);
        if (account == null || !passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid username or password");
        }
        return buildAuthResponse(account);
    }

    @Override
    public AuthTokenResponse refresh(RefreshTokenRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request 不能为空");
        }
        Claims claims = jwtTokenProvider.validateToken(request.refreshToken(), TokenType.REFRESH);
        Number accountId = claims.get("accountId", Number.class);
        if (accountId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Refresh token missing accountId");
        }
        AccountEntity account = accountMapper.selectById(accountId.longValue());
        if (account == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Account not found");
        }
        return buildAuthResponse(account);
    }

    @Override
    public Claims validateToken(String token) {
        return jwtTokenProvider.validateToken(token, TokenType.ACCESS);
    }

    @Override
    public AccountEntity getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUserPrincipal principal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }
        AccountEntity account = accountMapper.selectById(principal.accountId());
        if (account == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Account not found");
        }
        return account;
    }

    private AccountEntity findByUsername(String username) {
        return accountMapper.selectOne(new LambdaQueryWrapper<AccountEntity>()
            .eq(AccountEntity::getUsername, username));
    }

    private AuthTokenResponse buildAuthResponse(AccountEntity account) {
        return new AuthTokenResponse(
            account.getId(),
            account.getUsername(),
            jwtTokenProvider.createAccessToken(account),
            jwtTokenProvider.createRefreshToken(account),
            "Bearer",
            jwtTokenProvider.getAccessExpirationSeconds(),
            jwtTokenProvider.getRefreshExpirationSeconds()
        );
    }

    private String normalizeUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("username 不能为空");
        }
        return username.trim();
    }
}
