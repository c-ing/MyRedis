package com.redisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MyRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRedisApplication.class, args);
    }

}
