package com.zzx.fraud.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class FraudDetectionEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudDetectionEngineApplication.class, args);
    }
} 