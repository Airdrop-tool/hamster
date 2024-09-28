package com.cuongpq.hamster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HamsterApplication {

    public static void main(String[] args) {
        SpringApplication.run(HamsterApplication.class, args);
    }
}
