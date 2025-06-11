package com.zzx.fraud.engine.config;

import com.zzx.fraud.engine.model.FraudRule;
import com.zzx.fraud.engine.repository.FraudRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    /**
     * This bean is only activated when the "no-sql-init" profile is active.
     * When using SQL initialization (schema.sql and data.sql), this bean will not be created.
     */
    @Bean
    @Profile("no-sql-init")
    public CommandLineRunner initData(FraudRuleRepository fraudRuleRepository) {
        return args -> {
            logger.info("Checking if fraud detection rules need to be initialized...");

            // Check if rules already exist
            if (fraudRuleRepository.count() > 0) {
                logger.info("Fraud rules already exist. Skipping initialization.");
                return;
            }

            logger.info("Initializing fraud detection rules...");

            // High amount threshold rule
            FraudRule highAmountRule = new FraudRule(
                "High Amount Transaction",
                "Flags transactions with unusually high amounts",
                "AMOUNT_THRESHOLD",
                1000.0,
                null,
                true
            );
            fraudRuleRepository.save(highAmountRule);

            // Very high amount threshold rule
            FraudRule veryHighAmountRule = new FraudRule(
                "Very High Amount Transaction",
                "Flags transactions with extremely high amounts",
                "AMOUNT_THRESHOLD",
                5000.0,
                null,
                true
            );
            fraudRuleRepository.save(veryHighAmountRule);

            // Suspicious account rule
            FraudRule suspiciousAccountRule = new FraudRule(
                "Suspicious Account",
                "Flags transactions from known suspicious accounts",
                "SUSPICIOUS_ACCOUNT",
                null,
                "SUSP-", // Pattern for suspicious accounts that start with "SUSP-"
                true
            );
            fraudRuleRepository.save(suspiciousAccountRule);

            logger.info("Initialized {} fraud detection rules", fraudRuleRepository.count());
        };
    }
} 