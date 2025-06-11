package com.zzx.fraud.engine.service;

import com.zzx.fraud.engine.model.EnhancedTransactionRequest;
import com.zzx.fraud.engine.model.FraudDetectionResponse;
import com.zzx.fraud.engine.model.FraudDetectionResult;
import com.zzx.fraud.engine.model.FraudRule;
import com.zzx.fraud.engine.repository.FraudDetectionResultRepository;
import com.zzx.fraud.engine.repository.FraudRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class FraudDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionService.class);

    private final FraudRuleRepository fraudRuleRepository;
    private final FraudDetectionResultRepository fraudDetectionResultRepository;
    private final NotificationService notificationService;
    
    @Value("${notification.service.enabled:true}")
    private boolean notificationEnabled;
    
    private List<FraudRule> enabledRules;

    @Autowired
    public FraudDetectionService(FraudRuleRepository fraudRuleRepository,
                                FraudDetectionResultRepository fraudDetectionResultRepository,
                                NotificationService notificationService) {
        this.fraudRuleRepository = fraudRuleRepository;
        this.fraudDetectionResultRepository = fraudDetectionResultRepository;
        this.notificationService = notificationService;
    }
    
    @PostConstruct
    public void initialize() {
        loadEnabledRules();
        logger.info("Loaded {} enabled fraud detection rules", enabledRules.size());
    }
    
    private void loadEnabledRules() {
        this.enabledRules = fraudRuleRepository.findByEnabledTrue();
    }
    
    // Method to manually refresh rules if needed
    public void refreshRules() {
        loadEnabledRules();
        logger.info("Refreshed {} enabled fraud detection rules", enabledRules.size());
    }

    public FraudDetectionResponse detectFraud(EnhancedTransactionRequest request) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting fraud detection for transaction: {}", request.getTransactionId());

        // Create result object to store in database
        FraudDetectionResult result = new FraudDetectionResult(request.getTransactionId(), request.getAccountId());
        result.setTransactionAmount(request.getAmount());
        result.setIpAddress(request.getIpAddress());
        result.setDeviceId(request.getDeviceId());
        result.setCountryCode(request.getCountryCode());
        result.setDeviceType(request.getDeviceType());
        result.setChannel(request.getChannel());

        // Create response object
        FraudDetectionResponse response = new FraudDetectionResponse(request.getTransactionId(), false);
        
        try {
            // Apply rules
            boolean isFraudulent = false;
            List<String> triggeredRules = new ArrayList<>();
            
            for (FraudRule rule : enabledRules) {
                boolean ruleTriggered = applyRule(rule, request);
                if (ruleTriggered) {
                    isFraudulent = true; // Any triggered rule makes the transaction fraudulent
                    triggeredRules.add(rule.getName());
                    result.addTriggeredRule(rule.getName());
                    response.addTriggeredRule(rule.getName());
                    logger.debug("Rule triggered: {} for transaction: {}", rule.getName(), request.getTransactionId());
                }
            }
            
            result.setFraudulent(isFraudulent);
            response.setFraudulent(isFraudulent);
            
            if (isFraudulent) {
                response.setMessage("Transaction flagged as potentially fraudulent");
                logger.warn("Fraud detected for transaction: {}", request.getTransactionId());
                
                // If transaction is fraudulent, send to notification service
                if (notificationEnabled) {
                    boolean notificationSent = notificationService.sendFraudNotification(result);
                    if (notificationSent) {
                        logger.info("Fraud notification sent for transaction: {}", request.getTransactionId());
                    }
                }
            } else {
                response.setMessage("Transaction appears legitimate");
                logger.info("No fraud detected for transaction: {}", request.getTransactionId());
            }
            
        } catch (Exception e) {
            logger.error("Error during fraud detection for transaction: {}", request.getTransactionId(), e);
            response.setMessage("Error during fraud detection: " + e.getMessage());
        }
        
        // Calculate processing time
        long endTime = System.currentTimeMillis();
        long processingTime = endTime - startTime;
        
        result.setProcessingTimeMs(processingTime);
        response.setProcessingTimeMs(processingTime);
        
        // Save result to database
        fraudDetectionResultRepository.save(result);
        
        logger.info("Completed fraud detection for transaction: {} in {}ms", request.getTransactionId(), processingTime);
        return response;
    }
    
    private boolean applyRule(FraudRule rule, EnhancedTransactionRequest request) {
        switch (rule.getRuleType()) {
            case "AMOUNT_THRESHOLD":
                return applyAmountThresholdRule(rule, request);
            case "SUSPICIOUS_ACCOUNT":
                return applySuspiciousAccountRule(rule, request);
            default:
                logger.warn("Unknown rule type: {}", rule.getRuleType());
                return false;
        }
    }
    
    private boolean applyAmountThresholdRule(FraudRule rule, EnhancedTransactionRequest request) {
        if (rule.getThresholdValue() == null) {
            return false;
        }
        return request.getAmount() >= rule.getThresholdValue();
    }
    
    private boolean applySuspiciousAccountRule(FraudRule rule, EnhancedTransactionRequest request) {
        if (rule.getSuspiciousPattern() == null || request.getAccountId() == null) {
            return false;
        }
        try {
            Pattern pattern = Pattern.compile(rule.getSuspiciousPattern());
            return pattern.matcher(request.getAccountId()).find();
        } catch (Exception e) {
            logger.error("Error applying suspicious account rule: {}", e.getMessage(), e);
            return false;
        }
    }
} 