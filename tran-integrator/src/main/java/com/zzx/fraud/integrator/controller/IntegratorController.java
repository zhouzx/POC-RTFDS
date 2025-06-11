package com.zzx.fraud.integrator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zzx.fraud.integrator.model.EnhancedTransactionRequest;
import com.zzx.fraud.integrator.model.FraudDetectionResponse;
import com.zzx.fraud.integrator.model.TransactionRequest;
import com.zzx.fraud.integrator.service.TransactionEnhancementService;
import com.zzx.fraud.integrator.service.TransactionProducerService;

@RestController
@RequestMapping("/api/integrator")
public class IntegratorController {
    private static final Logger logger = LoggerFactory.getLogger(IntegratorController.class);
    
    @Autowired
    private TransactionEnhancementService enhancementService;
    
    @Autowired
    private TransactionProducerService producerService;
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Transaction Integrator is running");
    }
    
    /**
     * Process transaction manually
     * 
     * @param transaction Transaction to process
     * @return Processing result
     */
    @PostMapping("/process")
    public ResponseEntity<FraudDetectionResponse> processTransaction(@RequestBody TransactionRequest transaction) {
        logger.info("Manually processing transaction: {}", transaction.getTransactionId());
        
        try {
            // Enhance transaction data
            EnhancedTransactionRequest enhancedTransaction = enhancementService.enhanceTransaction(transaction);
            
            // Send to fraud detection engine
            FraudDetectionResponse response = producerService.sendToFraudDetectionEngine(enhancedTransaction);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Process enhanced transaction directly
     * 
     * @param enhancedTransaction Enhanced transaction to process
     * @return Processing result
     */
    @PostMapping("/process-enhanced")
    public ResponseEntity<FraudDetectionResponse> processEnhancedTransaction(
            @RequestBody EnhancedTransactionRequest enhancedTransaction) {
        logger.info("Manually processing enhanced transaction: {}", enhancedTransaction.getTransactionId());
        
        try {
            // Send enhanced transaction data directly to fraud-detection-engine
            FraudDetectionResponse fraudResponse = producerService.sendToFraudDetectionEngine(enhancedTransaction);
            
            if (fraudResponse != null) {
                logger.info("Enhanced transaction processed successfully: {}, Fraud Detection Result: {}",
                        enhancedTransaction.getTransactionId(),
                        fraudResponse.isFraudulent());
                return ResponseEntity.ok(fraudResponse);
            } else {
                logger.warn("No fraud detection result received: {}", enhancedTransaction.getTransactionId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception e) {
            logger.error("Error processing enhanced transaction: {}, Error: {}", 
                    enhancedTransaction.getTransactionId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
} 