package com.zzx.fraud.notify.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zzx.fraud.notify.model.FraudDetectionResult;
import com.zzx.fraud.notify.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Receive fraud detection results and process notifications
     * 
     * @param result Fraud detection result
     * @return Processing result
     */
    @PostMapping("/fraud-result")
    public ResponseEntity<String> receiveFraudResult(@RequestBody FraudDetectionResult result) {
        if (result == null || result.getTransactionId() == null) {
            logger.warn("Received invalid fraud detection result");
            return ResponseEntity.badRequest().body("Invalid fraud detection result");
        }
        
        logger.info("Received fraud detection result for transaction: {}", result.getTransactionId());
        
        try {
            notificationService.processNotification(result);
            return ResponseEntity.ok("Fraud notification received for transaction: " + result.getTransactionId());
        } catch (Exception e) {
            logger.error("Error processing notification", e);
            return ResponseEntity.internalServerError().body("Error processing notification: " + e.getMessage());
        }
    }
    
    /**
     * Health check endpoint
     * 
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification Service is running");
    }
} 