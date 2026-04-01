package com.dungeon.merchant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("com.dungeon.merchant.module")
public class DungeonMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(DungeonMerchantApplication.class, args);
    }
}
