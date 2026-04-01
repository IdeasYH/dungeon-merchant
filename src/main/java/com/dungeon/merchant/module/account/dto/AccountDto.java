package com.dungeon.merchant.module.account.dto;

import java.time.LocalDateTime;

public class AccountDto {

    private final Long id;
    private final String username;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AccountDto(Long id, String username, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
