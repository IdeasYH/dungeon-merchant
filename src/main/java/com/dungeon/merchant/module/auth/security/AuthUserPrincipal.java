package com.dungeon.merchant.module.auth.security;

import java.security.Principal;

public record AuthUserPrincipal(Long accountId, String username) implements Principal {

    @Override
    public String getName() {
        return username;
    }
}
