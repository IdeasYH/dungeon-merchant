package com.dungeon.merchant.module.auth.service;

import com.dungeon.merchant.module.auth.dto.request.LoginRequest;
import com.dungeon.merchant.module.auth.dto.request.RefreshTokenRequest;
import com.dungeon.merchant.module.auth.dto.request.RegisterRequest;
import com.dungeon.merchant.module.auth.dto.response.AuthTokenResponse;
import com.dungeon.merchant.module.auth.entity.AccountEntity;
import io.jsonwebtoken.Claims;

public interface AuthService {

    AuthTokenResponse register(RegisterRequest request);

    AuthTokenResponse login(LoginRequest request);

    AuthTokenResponse refresh(RefreshTokenRequest request);

    Claims validateToken(String token);

    AccountEntity getCurrentAccount();
}
