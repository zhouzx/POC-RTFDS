package com.zzx.fraud.integrator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zzx.fraud.integrator.model.EnhancedTransactionRequest;
import com.zzx.fraud.integrator.model.TransactionRequest;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class TransactionEnhancementService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionEnhancementService.class);
    
    // Simple mapping of IP address to country code
    private Map<String, String> ipToCountryMap = new HashMap<>();
    
    // Device ID to device type mapping
    private Map<String, String> deviceTypeMap = new HashMap<>();
    
    // Regular expression for IP address ranges
    private Pattern ipPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");
    
    // Random generator
    private Random random = new Random();
    
    @PostConstruct
    public void init() {
        // Initialize IP to country mapping
        ipToCountryMap.put("192.168", "US");
        ipToCountryMap.put("10.0", "CN");
        ipToCountryMap.put("172.16", "GB");
        ipToCountryMap.put("100.64", "JP");
        ipToCountryMap.put("169.254", "DE");
        ipToCountryMap.put("127.0", "FR");
        
        // Initialize device type mapping
        deviceTypeMap.put("DEV", "MOBILE");
        deviceTypeMap.put("PC", "DESKTOP");
        deviceTypeMap.put("TAB", "TABLET");
    }
    
    /**
     * Enhance transaction data
     * 
     * @param transaction Original transaction request
     * @return Enhanced transaction request
     */
    public EnhancedTransactionRequest enhanceTransaction(TransactionRequest transaction) {
        logger.info("Enhancing transaction data: {}", transaction.getTransactionId());
        
        EnhancedTransactionRequest enhancedTransaction = new EnhancedTransactionRequest(transaction);
        
        // Set country code
        enhancedTransaction.setCountryCode(determineCountryCode(transaction.getIpAddress()));
        
        // Set device type
        enhancedTransaction.setDeviceType(determineDeviceType(transaction.getDeviceId()));
        
        // Set transaction channel
        enhancedTransaction.setChannel("APP");
        
        // Randomly modify accountId with FRAUD- or BLACKLIST- prefix
        String originalAccountId = enhancedTransaction.getAccountId();
        String modifiedAccountId = randomlyModifyAccountId(originalAccountId);
        if (!originalAccountId.equals(modifiedAccountId)) {
            enhancedTransaction.setAccountId(modifiedAccountId);
            logger.info("Modified accountId from {} to {}", originalAccountId, modifiedAccountId);
        }
        
        logger.info("Transaction data enhancement completed: {}, Country: {}, Device Type: {}, Channel: {}",
                transaction.getTransactionId(), 
                enhancedTransaction.getCountryCode(),
                enhancedTransaction.getDeviceType(), 
                enhancedTransaction.getChannel());
        
        return enhancedTransaction;
    }
    
    /**
     * Randomly modify accountId with FRAUD- or BLACKLIST- prefix
     * 
     * @param accountId Original account ID
     * @return Modified account ID or original if no modification
     */
    private String randomlyModifyAccountId(String accountId) {
        // 10% chance to modify the accountId
        if (random.nextInt(10) == 0) {
            // 50% chance for each prefix
            if (random.nextBoolean()) {
                return "FRAUD-" + accountId;
            } else {
                return "BLACKLIST-" + accountId;
            }
        }
        return accountId;
    }
    
    /**
     * Determine country code based on IP address
     * 
     * @param ipAddress IP address
     * @return Country code
     */
    private String determineCountryCode(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return "UNKNOWN";
        }
        
        for (Map.Entry<String, String> entry : ipToCountryMap.entrySet()) {
            if (ipAddress.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Default return
        return "UNKNOWN";
    }
    
    /**
     * Determine device type based on device ID
     * 
     * @param deviceId Device ID
     * @return Device type
     */
    private String determineDeviceType(String deviceId) {
        if (deviceId == null || deviceId.isEmpty()) {
            return "UNKNOWN";
        }
        
        for (Map.Entry<String, String> entry : deviceTypeMap.entrySet()) {
            if (deviceId.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return "UNKNOWN";
    }
    
    /**
     * Determine transaction channel
     * 
     * @param transaction Transaction request
     * @return Channel type
     */
    private String determineChannel(TransactionRequest transaction) {
        // Here we can determine the channel based on various transaction characteristics
        // Simple example
        return "APP";
    }
} 