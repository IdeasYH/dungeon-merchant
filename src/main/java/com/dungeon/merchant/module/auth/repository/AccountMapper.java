package com.dungeon.merchant.module.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dungeon.merchant.module.auth.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {
}
