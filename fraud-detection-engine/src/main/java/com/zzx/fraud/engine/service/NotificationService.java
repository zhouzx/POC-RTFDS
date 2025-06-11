package com.zzx.fraud.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.zzx.fraud.engine.model.FraudDetectionResult;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${notification.service.url:http://localhost:8083}")
    private String notificationServiceUrl;
    
    @Value("${notification.service.endpoint:/api/notifications/fraud-result}")
    private String notificationEndpoint;
    
    /**
     * Send fraud detection results to notification service
     * 
     * @param result Fraud detection result
     * @return Whether sending was successful
     */
    public boolean sendFraudNotification(FraudDetectionResult result) {
        if (result == null || !result.isFraudulent()) {
            // Only send fraudulent transactions
            return false;
        }
        
        String url = notificationServiceUrl + notificationEndpoint;
        logger.info("Sending fraud detection result to notification service: {}, URL: {}", result.getTransactionId(), url);
        
        try {
            // Print JSON
            String json = objectMapper.writeValueAsString(result);
            logger.info("Sending JSON: {}", json);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<FraudDetectionResult> request = new HttpEntity<>(result, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Fraud detection result sent successfully: {}", result.getTransactionId());
                return true;
            } else {
                logger.warn("Fraud detection result returned non-success status code: {}, response status: {}", 
                        result.getTransactionId(), response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to send fraud detection result: {}, error: {}", result.getTransactionId(), e.getMessage(), e);
            return false;
        }
    }
} 