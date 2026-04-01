package com.dungeon.merchant.module.account.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dungeon.merchant.module.account.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
