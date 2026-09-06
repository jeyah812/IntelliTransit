package com.intellitransit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IntelliTransitApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliTransitApplication.class, args);
    }
}
