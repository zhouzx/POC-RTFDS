package com.zzx.fraud.integrator.controller;

import com.zzx.fraud.integrator.model.EnhancedTransactionRequest;
import com.zzx.fraud.integrator.model.FraudDetectionResponse;
import com.zzx.fraud.integrator.model.TransactionRequest;
import com.zzx.fraud.integrator.service.TransactionEnhancementService;
import com.zzx.fraud.integrator.service.TransactionProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegratorControllerTest {

    @Mock
    private TransactionEnhancementService enhancementService;

    @Mock
    private TransactionProducerService producerService;

    @InjectMocks
    private IntegratorController integratorController;

    private TransactionRequest transaction;
    private EnhancedTransactionRequest enhancedTransaction;
    private FraudDetectionResponse fraudResponse;

    @BeforeEach
    void setUp() {
        // Create test request
        transaction = new TransactionRequest();
        transaction.setTransactionId("TX-TEST-001");
        transaction.setAccountId("ACC-TEST-001");
        transaction.setAmount(1000.0);
        transaction.setIpAddress("192.168.1.1");
        transaction.setDeviceId("DEV12345");
        
        // Create enhanced transaction request
        enhancedTransaction = new EnhancedTransactionRequest();
        enhancedTransaction.setTransactionId("TX-TEST-001");
        enhancedTransaction.setAccountId("ACC-TEST-001");
        enhancedTransaction.setAmount(1000.0);
        enhancedTransaction.setIpAddress("192.168.1.1");
        enhancedTransaction.setDeviceId("DEV12345");
        enhancedTransaction.setCountryCode("US");
        enhancedTransaction.setDeviceType("MOBILE");
        enhancedTransaction.setChannel("APP");
        
        // Create fraud detection response
        fraudResponse = new FraudDetectionResponse();
        fraudResponse.setTransactionId("TX-TEST-001");
        fraudResponse.setFraudulent(true);
        fraudResponse.setMessage("Transaction flagged as potentially fraudulent");
    }

    @Test
    void processTransaction_withValidTransaction_shouldReturnResponse() {
        // Set up mock behavior
        when(enhancementService.enhanceTransaction(any(TransactionRequest.class))).thenReturn(enhancedTransaction);
        when(producerService.sendToFraudDetectionEngine(any(EnhancedTransactionRequest.class))).thenReturn(fraudResponse);
        
        // Execute test
        ResponseEntity<FraudDetectionResponse> responseEntity = integratorController.processTransaction(transaction);
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isFraudulent());
        
        // Verify interactions
        verify(enhancementService).enhanceTransaction(transaction);
        verify(producerService).sendToFraudDetectionEngine(enhancedTransaction);
    }

    @Test
    void processTransaction_withException_shouldReturnInternalServerError() {
        // Set up mock behavior
        when(enhancementService.enhanceTransaction(any(TransactionRequest.class))).thenThrow(new RuntimeException("Test exception"));
        
        // Execute test
        ResponseEntity<FraudDetectionResponse> responseEntity = integratorController.processTransaction(transaction);
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        
        // Verify interactions
        verify(enhancementService).enhanceTransaction(transaction);
        verify(producerService, never()).sendToFraudDetectionEngine(any(EnhancedTransactionRequest.class));
    }

    @Test
    void processEnhancedTransaction_withValidTransaction_shouldReturnResponse() {
        // Set up mock behavior
        when(producerService.sendToFraudDetectionEngine(any(EnhancedTransactionRequest.class))).thenReturn(fraudResponse);
        
        // Execute test
        ResponseEntity<FraudDetectionResponse> responseEntity = integratorController.processEnhancedTransaction(enhancedTransaction);
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isFraudulent());
        
        // Verify interactions
        verify(producerService).sendToFraudDetectionEngine(enhancedTransaction);
    }

    @Test
    void processEnhancedTransaction_withNullResponse_shouldReturnInternalServerError() {
        // Set up mock behavior
        when(producerService.sendToFraudDetectionEngine(any(EnhancedTransactionRequest.class))).thenReturn(null);
        
        // Execute test
        ResponseEntity<FraudDetectionResponse> responseEntity = integratorController.processEnhancedTransaction(enhancedTransaction);
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        
        // Verify interactions
        verify(producerService).sendToFraudDetectionEngine(enhancedTransaction);
    }

    @Test
    void health_shouldReturnOk() {
        // Execute test
        ResponseEntity<String> responseEntity = integratorController.health();
        
        // Verify results
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Transaction Integrator is running", responseEntity.getBody());
    }
} 