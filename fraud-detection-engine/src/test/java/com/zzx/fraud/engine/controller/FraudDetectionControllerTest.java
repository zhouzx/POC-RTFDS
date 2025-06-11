package com.zzx.fraud.engine.controller;

import com.zzx.fraud.engine.model.EnhancedTransactionRequest;
import com.zzx.fraud.engine.model.FraudDetectionResponse;
import com.zzx.fraud.engine.service.FraudDetectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudDetectionControllerTest {

    @Mock
    private FraudDetectionService fraudDetectionService;

    @InjectMocks
    private FraudDetectionController fraudDetectionController;

    private EnhancedTransactionRequest transactionRequest;
    private FraudDetectionResponse fraudResponse;
    private FraudDetectionResponse nonFraudResponse;

    @BeforeEach
    void setUp() {
        // Create test request
        transactionRequest = new EnhancedTransactionRequest();
        transactionRequest.setTransactionId("TX-TEST-001");
        transactionRequest.setAccountId("ACC-TEST-001");
        transactionRequest.setAmount(5000.0);
        
        // Create fraud response
        fraudResponse = new FraudDetectionResponse(transactionRequest.getTransactionId(), true);
        fraudResponse.setMessage("Transaction flagged as potentially fraudulent");
        List<String> triggeredRules = new ArrayList<>();
        triggeredRules.add("Very High Amount Transaction");
        fraudResponse.setTriggeredRules(triggeredRules);
        fraudResponse.setProcessingTimeMs(100L);
        
        // Create non-fraud response
        nonFraudResponse = new FraudDetectionResponse(transactionRequest.getTransactionId(), false);
        nonFraudResponse.setMessage("Transaction appears legitimate");
        nonFraudResponse.setProcessingTimeMs(50L);
    }

    @Test
    void detectFraud_withFraudulentTransaction_shouldReturnFraudResponse() {
        // Set up mock behavior
        when(fraudDetectionService.detectFraud(any(EnhancedTransactionRequest.class))).thenReturn(fraudResponse);
        
        // Execute test
        ResponseEntity<FraudDetectionResponse> responseEntity = fraudDetectionController.detectFraud(transactionRequest);
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isFraudulent());
        assertEquals("Transaction flagged as potentially fraudulent", responseEntity.getBody().getMessage());
        assertEquals(1, responseEntity.getBody().getTriggeredRules().size());
        
        // Verify interactions
        verify(fraudDetectionService).detectFraud(transactionRequest);
    }

    @Test
    void detectFraud_withLegitimateTransaction_shouldReturnNonFraudResponse() {
        // Set up mock behavior
        when(fraudDetectionService.detectFraud(any(EnhancedTransactionRequest.class))).thenReturn(nonFraudResponse);
        
        // Execute test
        ResponseEntity<FraudDetectionResponse> responseEntity = fraudDetectionController.detectFraud(transactionRequest);
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertFalse(responseEntity.getBody().isFraudulent());
        assertEquals("Transaction appears legitimate", responseEntity.getBody().getMessage());
        
        // Verify interactions
        verify(fraudDetectionService).detectFraud(transactionRequest);
    }

    @Test
    void refreshRules_shouldReturnSuccessMessage() {
        // Set up mock behavior
        doNothing().when(fraudDetectionService).refreshRules();
        
        // Execute test
        ResponseEntity<String> responseEntity = fraudDetectionController.refreshRules();
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Fraud detection rules refreshed successfully", responseEntity.getBody());
        
        // Verify interactions
        verify(fraudDetectionService).refreshRules();
    }

    @Test
    void healthCheck_shouldReturnOk() {
        // Execute test
        ResponseEntity<String> responseEntity = fraudDetectionController.healthCheck();
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Fraud Detection Engine is running", responseEntity.getBody());
    }
} 