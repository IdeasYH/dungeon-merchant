package com.dungeon.merchant.module.account.service;

import com.dungeon.merchant.module.account.dto.AccountDto;
import com.dungeon.merchant.module.account.dto.UpdateAccountRequest;

public interface AccountService {

    AccountDto getAccount(Long accountId);

    void updateAccount(Long accountId, UpdateAccountRequest request);
}
