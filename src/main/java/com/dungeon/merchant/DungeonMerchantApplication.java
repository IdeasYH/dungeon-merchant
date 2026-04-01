package com.dungeon.merchant;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan(
    basePackages = "com.dungeon.merchant.module",
    annotationClass = Mapper.class,
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class DungeonMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(DungeonMerchantApplication.class, args);
    }
}
