package com.dungeon.merchant.module.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateAccountRequest {

    @NotBlank(message = "username 不能为空")
    @Size(max = 50, message = "username 长度不能超过 50")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
