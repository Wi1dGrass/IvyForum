package com.ivy.forum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.ivy.forum.mapper")
@EnableScheduling
public class SchoolForumApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolForumApplication.class, args);
    }
}