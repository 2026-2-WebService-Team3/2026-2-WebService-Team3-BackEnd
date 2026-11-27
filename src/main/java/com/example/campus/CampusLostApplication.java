package com.example.campus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CampusLostApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusLostApplication.class, args);
    }
}