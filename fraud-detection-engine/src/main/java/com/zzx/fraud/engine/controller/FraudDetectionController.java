package com.zzx.fraud.engine.controller;

import com.zzx.fraud.engine.model.EnhancedTransactionRequest;
import com.zzx.fraud.engine.model.FraudDetectionResponse;
import com.zzx.fraud.engine.service.FraudDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fraud")
public class FraudDetectionController {

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionController.class);
    
    private final FraudDetectionService fraudDetectionService;
    
    @Autowired
    public FraudDetectionController(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }
    
    @PostMapping("/detect")
    public ResponseEntity<FraudDetectionResponse> detectFraud(@RequestBody EnhancedTransactionRequest request) {
        logger.info("Received fraud detection request for transaction: {}", request.getTransactionId());
        FraudDetectionResponse response = fraudDetectionService.detectFraud(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/rules/refresh")
    public ResponseEntity<String> refreshRules() {
        logger.info("Refreshing fraud detection rules");
        fraudDetectionService.refreshRules();
        return ResponseEntity.ok("Fraud detection rules refreshed successfully");
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Fraud Detection Engine is running");
    }
} 