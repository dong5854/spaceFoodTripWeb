package com.triplenova.spacefoodtrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpaceFoodTripApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaceFoodTripApplication.class, args);
    }

}
