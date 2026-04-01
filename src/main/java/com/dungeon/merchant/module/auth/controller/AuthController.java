package com.dungeon.merchant.module.auth.controller;

import com.dungeon.merchant.module.auth.dto.request.LoginRequest;
import com.dungeon.merchant.module.auth.dto.request.RefreshTokenRequest;
import com.dungeon.merchant.module.auth.dto.request.RegisterRequest;
import com.dungeon.merchant.module.auth.dto.response.AuthTokenResponse;
import com.dungeon.merchant.module.auth.service.AuthService;
import com.dungeon.merchant.module.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthTokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }
}
