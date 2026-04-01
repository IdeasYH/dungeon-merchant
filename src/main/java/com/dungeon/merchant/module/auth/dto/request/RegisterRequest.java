package com.dungeon.merchant.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "username 不能为空")
    @Size(min = 3, max = 50, message = "username 长度必须在 3 到 50 之间")
    String username,

    @NotBlank(message = "password 不能为空")
    @Size(min = 8, max = 100, message = "password 长度必须在 8 到 100 之间")
    String password
) {
}
