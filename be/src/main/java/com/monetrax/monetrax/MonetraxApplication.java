package com.monetrax.monetrax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MonetraxApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonetraxApplication.class, args);
    }

}
