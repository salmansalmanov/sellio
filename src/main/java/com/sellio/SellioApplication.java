package com.sellio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class SellioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SellioApplication.class, args);
    }

}
