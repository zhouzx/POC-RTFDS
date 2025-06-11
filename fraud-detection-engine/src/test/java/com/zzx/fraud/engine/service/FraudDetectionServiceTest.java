package com.zzx.fraud.engine.service;

import com.zzx.fraud.engine.model.EnhancedTransactionRequest;
import com.zzx.fraud.engine.model.FraudDetectionResponse;
import com.zzx.fraud.engine.model.FraudDetectionResult;
import com.zzx.fraud.engine.model.FraudRule;
import com.zzx.fraud.engine.repository.FraudDetectionResultRepository;
import com.zzx.fraud.engine.repository.FraudRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {

    @Mock
    private FraudRuleRepository ruleRepository;

    @Mock
    private FraudDetectionResultRepository resultRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    private EnhancedTransactionRequest transaction;
    private FraudRule amountRule;
    private FraudRule accountRule;
    private FraudRule blacklistRule;

    @BeforeEach
    void setUp() {
        // Set notification service as enabled
        ReflectionTestUtils.setField(fraudDetectionService, "notificationEnabled", true);
        
        // Create test rules
        List<FraudRule> rules = new ArrayList<>();
        
        // Amount threshold rule
        amountRule = new FraudRule();
        amountRule.setId(1L);
        amountRule.setName("AMOUNT_THRESHOLD");
        amountRule.setDescription("Transaction amount exceeds threshold");
        amountRule.setRuleType("AMOUNT_THRESHOLD");
        amountRule.setThresholdValue(1000.0);
        amountRule.setEnabled(true);
        rules.add(amountRule);
        
        // Suspicious account rule for FRAUD- pattern
        accountRule = new FraudRule();
        accountRule.setId(2L);
        accountRule.setName("SUSPICIOUS_ACCOUNT");
        accountRule.setDescription("Account pattern matches suspicious pattern");
        accountRule.setRuleType("SUSPICIOUS_ACCOUNT");
        accountRule.setSuspiciousPattern("FRAUD-.*");
        accountRule.setEnabled(true);
        rules.add(accountRule);
        
        // Add new rule for BLACKLIST pattern
        blacklistRule = new FraudRule();
        blacklistRule.setId(3L);
        blacklistRule.setName("BLACKLIST_ACCOUNT");
        blacklistRule.setDescription("Account pattern matches blacklist pattern");
        blacklistRule.setRuleType("SUSPICIOUS_ACCOUNT");
        blacklistRule.setSuspiciousPattern("BLACKLIST-.*");
        blacklistRule.setEnabled(true);
        rules.add(blacklistRule);
        
        // Create test transaction
        transaction = new EnhancedTransactionRequest();
        transaction.setTransactionId("TX-TEST-001");
        transaction.setAccountId("ACC-TEST-001");
        transaction.setAmount(1500.0);
        transaction.setIpAddress("192.168.1.1");
        transaction.setDeviceId("DEV12345");
        transaction.setCountryCode("US");
        transaction.setDeviceType("MOBILE");
        transaction.setChannel("APP");
        
        // Set up mock behavior, using lenient() to allow unused stubs
        lenient().when(ruleRepository.findAll()).thenReturn(rules);
        lenient().when(ruleRepository.findByEnabledTrue()).thenReturn(rules);
        lenient().when(resultRepository.save(any(FraudDetectionResult.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void detectFraud_withAmountExceedingThreshold_shouldFlagAsFraudulent() {
        // Initialize rules
        fraudDetectionService.refreshRules();
        
        // Execute test
        FraudDetectionResponse response = fraudDetectionService.detectFraud(transaction);
        
        // Verify results
        assertNotNull(response);
        assertTrue(response.isFraudulent());
        assertEquals(transaction.getTransactionId(), response.getTransactionId());
        assertTrue(response.getTriggeredRules().contains("AMOUNT_THRESHOLD"));
        
        // Verify interactions
        verify(resultRepository).save(any(FraudDetectionResult.class));
        verify(notificationService).sendFraudNotification(any(FraudDetectionResult.class));
    }

    @Test
    void detectFraud_withSuspiciousFraudAccountPattern_shouldFlagAsFraudulent() {
        // Initialize rules
        fraudDetectionService.refreshRules();
        transaction.setAccountId("FRAUD-12345");
        transaction.setAmount(500.0); // Below threshold
        
        // Execute test
        FraudDetectionResponse response = fraudDetectionService.detectFraud(transaction);
        
        // Verify results
        assertNotNull(response);
        assertTrue(response.isFraudulent());
        assertEquals(transaction.getTransactionId(), response.getTransactionId());
        assertTrue(response.getTriggeredRules().contains("SUSPICIOUS_ACCOUNT"));
        
        // Verify interactions
        verify(resultRepository).save(any(FraudDetectionResult.class));
        verify(notificationService).sendFraudNotification(any(FraudDetectionResult.class));
    }
    
    @Test
    void detectFraud_withSuspiciousBlacklistAccountPattern_shouldFlagAsFraudulent() {
        // Initialize rules
        fraudDetectionService.refreshRules();
        transaction.setAccountId("BLACKLIST-12345");
        transaction.setAmount(500.0); // Below threshold
        
        // Execute test
        FraudDetectionResponse response = fraudDetectionService.detectFraud(transaction);
        
        // Verify results
        assertNotNull(response);
        assertTrue(response.isFraudulent());
        assertEquals(transaction.getTransactionId(), response.getTransactionId());
        assertTrue(response.getTriggeredRules().contains("BLACKLIST_ACCOUNT"));
        
        // Verify interactions
        verify(resultRepository).save(any(FraudDetectionResult.class));
        verify(notificationService).sendFraudNotification(any(FraudDetectionResult.class));
    }
    
    @Test
    void detectFraud_withCombinedPattern_shouldFlagAsFraudulent() {
        // Test the FRAUD-|BLACKLIST- pattern from data.sql
        // Initialize rules with a combined pattern rule
        List<FraudRule> combinedRules = new ArrayList<>();
        
        FraudRule combinedRule = new FraudRule();
        combinedRule.setId(4L);
        combinedRule.setName("COMBINED_PATTERN");
        combinedRule.setDescription("Combined suspicious patterns");
        combinedRule.setRuleType("SUSPICIOUS_ACCOUNT");
        combinedRule.setSuspiciousPattern("FRAUD-|BLACKLIST-");
        combinedRule.setEnabled(true);
        
        combinedRules.add(combinedRule);
        
        when(ruleRepository.findByEnabledTrue()).thenReturn(combinedRules);
        fraudDetectionService.refreshRules();
        
        // Test with FRAUD pattern
        transaction.setAccountId("FRAUD-ACCOUNT-123");
        transaction.setAmount(500.0); // Below threshold
        
        // Execute test
        FraudDetectionResponse response = fraudDetectionService.detectFraud(transaction);
        
        // Verify results
        assertNotNull(response);
        assertTrue(response.isFraudulent());
        assertEquals(transaction.getTransactionId(), response.getTransactionId());
        assertTrue(response.getTriggeredRules().contains("COMBINED_PATTERN"));
        
        // Test with BLACKLIST pattern
        transaction.setAccountId("BLACKLIST-ACCOUNT-456");
        
        // Execute test
        response = fraudDetectionService.detectFraud(transaction);
        
        // Verify results
        assertNotNull(response);
        assertTrue(response.isFraudulent());
        assertEquals(transaction.getTransactionId(), response.getTransactionId());
        assertTrue(response.getTriggeredRules().contains("COMBINED_PATTERN"));
    }

    @Test
    void detectFraud_withNoRulesTriggered_shouldNotFlagAsFraudulent() {
        // Initialize rules
        fraudDetectionService.refreshRules();
        transaction.setAccountId("NORMAL-12345");
        transaction.setAmount(500.0); // Below threshold
        
        // Execute test
        FraudDetectionResponse response = fraudDetectionService.detectFraud(transaction);
        
        // Verify results
        assertNotNull(response);
        assertFalse(response.isFraudulent());
        assertEquals(transaction.getTransactionId(), response.getTransactionId());
        assertTrue(response.getTriggeredRules().isEmpty());
        
        // Verify interactions
        verify(resultRepository).save(any(FraudDetectionResult.class));
        verify(notificationService, never()).sendFraudNotification(any(FraudDetectionResult.class));
    }

    @Test
    void refreshRules_shouldUpdateRulesFromRepository() {
        // Initialize rules
        fraudDetectionService.refreshRules();
        
        // Change mock return value
        List<FraudRule> updatedRules = new ArrayList<>();
        updatedRules.add(amountRule);
        when(ruleRepository.findByEnabledTrue()).thenReturn(updatedRules);
        
        // Execute refresh
        fraudDetectionService.refreshRules();
        
        // Verify interactions
        verify(ruleRepository, times(2)).findByEnabledTrue();
    }
    
    @Test
    void applyRule_withUnknownRuleType_shouldReturnFalse() {
        // Setup rule with unknown type
        FraudRule unknownRule = new FraudRule();
        unknownRule.setId(5L);
        unknownRule.setName("UNKNOWN_RULE");
        unknownRule.setDescription("Rule with unknown type");
        unknownRule.setRuleType("UNKNOWN_TYPE");
        unknownRule.setEnabled(true);
        
        // Access private method using reflection
        try {
            java.lang.reflect.Method applyRuleMethod = FraudDetectionService.class.getDeclaredMethod(
                    "applyRule", FraudRule.class, EnhancedTransactionRequest.class);
            applyRuleMethod.setAccessible(true);
            
            // Execute test
            boolean result = (boolean) applyRuleMethod.invoke(fraudDetectionService, unknownRule, transaction);
            
            // Verify result
            assertFalse(result);
        } catch (Exception e) {
            fail("Failed to invoke private method: " + e.getMessage());
        }
    }
    
    @Test
    void applySuspiciousAccountRule_withNullPattern_shouldReturnFalse() {
        // Setup rule with null pattern
        FraudRule nullPatternRule = new FraudRule();
        nullPatternRule.setId(6L);
        nullPatternRule.setName("NULL_PATTERN");
        nullPatternRule.setDescription("Rule with null pattern");
        nullPatternRule.setRuleType("SUSPICIOUS_ACCOUNT");
        nullPatternRule.setSuspiciousPattern(null);
        nullPatternRule.setEnabled(true);
        
        // Access private method using reflection
        try {
            java.lang.reflect.Method applySuspiciousAccountRuleMethod = FraudDetectionService.class.getDeclaredMethod(
                    "applySuspiciousAccountRule", FraudRule.class, EnhancedTransactionRequest.class);
            applySuspiciousAccountRuleMethod.setAccessible(true);
            
            // Execute test
            boolean result = (boolean) applySuspiciousAccountRuleMethod.invoke(
                    fraudDetectionService, nullPatternRule, transaction);
            
            // Verify result
            assertFalse(result);
        } catch (Exception e) {
            fail("Failed to invoke private method: " + e.getMessage());
        }
    }
    
    @Test
    void applySuspiciousAccountRule_withInvalidPattern_shouldReturnFalseAndLogError() {
        // Setup rule with invalid regex pattern
        FraudRule invalidPatternRule = new FraudRule();
        invalidPatternRule.setId(7L);
        invalidPatternRule.setName("INVALID_PATTERN");
        invalidPatternRule.setDescription("Rule with invalid pattern");
        invalidPatternRule.setRuleType("SUSPICIOUS_ACCOUNT");
        invalidPatternRule.setSuspiciousPattern("["); // Invalid regex
        invalidPatternRule.setEnabled(true);
        
        // Access private method using reflection
        try {
            java.lang.reflect.Method applySuspiciousAccountRuleMethod = FraudDetectionService.class.getDeclaredMethod(
                    "applySuspiciousAccountRule", FraudRule.class, EnhancedTransactionRequest.class);
            applySuspiciousAccountRuleMethod.setAccessible(true);
            
            // Execute test
            boolean result = (boolean) applySuspiciousAccountRuleMethod.invoke(
                    fraudDetectionService, invalidPatternRule, transaction);
            
            // Verify result
            assertFalse(result);
        } catch (Exception e) {
            fail("Failed to invoke private method: " + e.getMessage());
        }
    }
} 