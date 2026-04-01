package com.dungeon.merchant.module.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dungeon.merchant.exception.BusinessException;
import com.dungeon.merchant.module.account.dto.AccountDto;
import com.dungeon.merchant.module.account.dto.UpdateAccountRequest;
import com.dungeon.merchant.module.account.entity.Account;
import com.dungeon.merchant.module.account.repository.AccountMapper;
import com.dungeon.merchant.module.account.service.AccountService;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountDto getAccount(Long accountId) {
        validateAccountId(accountId);
        return toDto(requireAccount(accountId));
    }

    @Override
    @Transactional
    public void updateAccount(Long accountId, UpdateAccountRequest request) {
        validateAccountId(accountId);
        if (request == null || !StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("username 不能为空");
        }

        Account account = requireAccount(accountId);
        String username = request.getUsername().trim();
        ensureUsernameAvailable(username, accountId);

        account.setUsername(username);
        account.setUpdatedAt(LocalDateTime.now());
        accountMapper.updateById(account);
    }

    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new IllegalArgumentException("accountId 不能为空");
        }
    }

    private Account requireAccount(Long accountId) {
        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "账户不存在");
        }
        return account;
    }

    private void ensureUsernameAvailable(String username, Long accountId) {
        Long count = accountMapper.selectCount(new LambdaQueryWrapper<Account>()
            .eq(Account::getUsername, username)
            .ne(Account::getId, accountId));
        if (count != null && count > 0) {
            throw new BusinessException(HttpStatus.CONFLICT, "用户名已存在");
        }
    }

    private AccountDto toDto(Account account) {
        return new AccountDto(
            account.getId(),
            account.getUsername(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}
