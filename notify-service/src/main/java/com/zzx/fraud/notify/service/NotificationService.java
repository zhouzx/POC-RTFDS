package com.zzx.fraud.notify.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zzx.fraud.notify.model.FraudDetectionResult;

@Service
public class NotificationService {

    // Changed from private static final to allow for testing/mocking
    static Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final Logger fraudLogger = LoggerFactory.getLogger("FRAUD_ALERTS");

    /**
     * Process fraud detection results and log them
     * 
     * @param result Fraud detection result
     */
    public void processNotification(FraudDetectionResult result) {
        if (result == null) {
            logger.warn("Received null fraud detection result");
            return;
        }

        if (result.isFraudulent()) {
            // Log fraudulent transaction to dedicated fraud log
            logger.warn("FRAUD ALERT: Transaction {} from account {} flagged as fraudulent",
                    result.getTransactionId(), result.getAccountId());
            
            // Log more detailed information
            logger.warn("FRAUD DETAILS: {}", result);
            
            // Log triggered rules
            if (result.getTriggeredRules() != null && !result.getTriggeredRules().isEmpty()) {
                logger.warn("FRAUD RULES TRIGGERED: {} for transaction {}",
                        result.getTriggeredRules(), result.getTransactionId());
            }
            
            // Additional notification logic could be added here (email, SMS, etc.)
            
            logger.info("Processed fraud notification for transaction: {}", result.getTransactionId());
        } else {
            // Log non-fraudulent transaction
            logger.info("Processed non-fraudulent notification for transaction: {}", result.getTransactionId());
        }
    }
} 