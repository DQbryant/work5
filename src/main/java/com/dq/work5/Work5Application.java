package com.dq.work5;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.dq.work5.mapper")
@SpringBootApplication
public class Work5Application {

    public static void main(String[] args) {
        SpringApplication.run(Work5Application.class, args);
    }

}
