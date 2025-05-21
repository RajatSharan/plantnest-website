package com.plantnest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.plantnest.model")
@EnableJpaRepositories(basePackages = "com.plantnest.repository")

public class PlantNestApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlantNestApplication.class, args);
    }
}
