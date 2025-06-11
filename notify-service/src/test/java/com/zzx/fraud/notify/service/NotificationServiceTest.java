package com.zzx.fraud.notify.service;

import com.zzx.fraud.notify.model.FraudDetectionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private Logger logger;

    @InjectMocks
    private NotificationService notificationService;

    private FraudDetectionResult fraudResult;

    @BeforeEach
    void setUp() {
        // Create test fraud result
        fraudResult = new FraudDetectionResult();
        fraudResult.setTransactionId("TX-TEST-001");
        fraudResult.setAccountId("ACC-TEST-001");
        fraudResult.setFraudulent(true);
        Set<String> rules = new HashSet<>(Arrays.asList("Very High Amount Transaction"));
        fraudResult.setTriggeredRules(rules);
        fraudResult.setTransactionAmount(10000.0);
        fraudResult.setIpAddress("192.168.1.1");
        fraudResult.setDeviceId("DEV12345");
        fraudResult.setCountryCode("US");
        fraudResult.setDeviceType("MOBILE");
        fraudResult.setChannel("APP");
        fraudResult.setProcessedAt(LocalDateTime.now());
    }

    @Test
    void processNotification_withFraudulentTransaction_shouldLogAlerts() {
        // Set the logger field using reflection since it's a static final field
        try {
            java.lang.reflect.Field loggerField = NotificationService.class.getDeclaredField("logger");
            loggerField.setAccessible(true);
            loggerField.set(null, logger);
        } catch (Exception e) {
            // Handle reflection exception
        }
        
        // Execute the test
        notificationService.processNotification(fraudResult);
        
        // Verify logs were called
        verify(logger).warn(contains("FRAUD ALERT"), anyString(), anyString());
        verify(logger).warn(contains("FRAUD DETAILS"), any(FraudDetectionResult.class));
        verify(logger).warn(contains("FRAUD RULES TRIGGERED"), any(), anyString());
        verify(logger).info(contains("Processed fraud notification"), anyString());
    }

    @Test
    void processNotification_withNonFraudulentTransaction_shouldLogInfo() {
        // Set the logger field using reflection since it's a static final field
        try {
            java.lang.reflect.Field loggerField = NotificationService.class.getDeclaredField("logger");
            loggerField.setAccessible(true);
            loggerField.set(null, logger);
        } catch (Exception e) {
            // Handle reflection exception
        }
        
        // Set transaction as non-fraudulent
        fraudResult.setFraudulent(false);
        
        // Execute the test
        notificationService.processNotification(fraudResult);
        
        // Verify only info log was called
        verify(logger).info(contains("Processed non-fraudulent notification"), anyString());
    }
} 