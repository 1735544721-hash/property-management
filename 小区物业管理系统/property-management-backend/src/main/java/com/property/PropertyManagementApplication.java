package com.property;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 小区物业管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.property.mapper")
public class PropertyManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PropertyManagementApplication.class, args);
        System.out.println("=================================");
        System.out.println("  小区物业管理系统启动成功！");
        System.out.println("=================================");
    }
}

