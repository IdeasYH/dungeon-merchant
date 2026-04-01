package com.dungeon.merchant.module.account.controller;

import com.dungeon.merchant.module.account.dto.AccountDto;
import com.dungeon.merchant.module.account.dto.UpdateAccountRequest;
import com.dungeon.merchant.module.account.service.AccountService;
import com.dungeon.merchant.module.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ApiResponse<AccountDto> getAccount(@RequestAttribute("accountId") Long accountId) {
        return ApiResponse.success(accountService.getAccount(accountId));
    }

    @PutMapping
    public ApiResponse<Void> updateAccount(
        @RequestAttribute("accountId") Long accountId,
        @Valid @RequestBody UpdateAccountRequest request
    ) {
        accountService.updateAccount(accountId, request);
        return ApiResponse.success(null);
    }
}
