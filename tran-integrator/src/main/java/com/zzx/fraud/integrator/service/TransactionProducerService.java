package com.zzx.fraud.integrator.service;

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

import com.zzx.fraud.integrator.model.EnhancedTransactionRequest;
import com.zzx.fraud.integrator.model.FraudDetectionResponse;

@Service
public class TransactionProducerService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProducerService.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${fraud-detection.url:http://localhost:8080/api/fraud/detect}")
    private String fraudDetectionUrl;
    
    /**
     * Send enhanced transaction data to fraud-detection-engine via RESTful API
     * 
     * @param enhancedTransaction Enhanced transaction request
     * @return Fraud detection result
     */
    public FraudDetectionResponse sendToFraudDetectionEngine(EnhancedTransactionRequest enhancedTransaction) {
        String url = fraudDetectionUrl;
        logger.info("Sending enhanced transaction data to fraud-detection-engine: {}, URL: {}", 
                enhancedTransaction.getTransactionId(), url);
        
        try {
            ResponseEntity<FraudDetectionResponse> response = restTemplate.postForEntity(
                    url,
                    enhancedTransaction,
                    FraudDetectionResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                FraudDetectionResponse fraudResponse = response.getBody();
                logger.info("Enhanced transaction data sent successfully: {}, Fraud Detection Result: {}",
                        enhancedTransaction.getTransactionId(),
                        fraudResponse != null ? fraudResponse.isFraudulent() : "Unknown");
                return fraudResponse;
            } else {
                logger.warn("Enhanced transaction data sent but received non-success status code: {}, Response Status: {}",
                        enhancedTransaction.getTransactionId(), response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to send enhanced transaction data: {}, Error: {}", 
                    enhancedTransaction.getTransactionId(), e.getMessage(), e);
            return null;
        }
    }
} 