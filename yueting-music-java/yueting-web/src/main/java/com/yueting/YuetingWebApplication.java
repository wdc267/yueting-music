package com.yueting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"com.yueting.mapper"})
@SpringBootApplication(scanBasePackages = "com.yueting")
public class YuetingWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuetingWebApplication.class, args);
    }
}
