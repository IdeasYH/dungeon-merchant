package com.dungeon.merchant.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "username 不能为空")
    @Size(max = 50, message = "username 长度不能超过 50")
    String username,

    @NotBlank(message = "password 不能为空")
    String password
) {
}
