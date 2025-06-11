package com.zzx.fraud.notify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzx.fraud.notify.model.FraudDetectionResult;
import com.zzx.fraud.notify.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private NotificationService notificationService;

    private FraudDetectionResult fraudResult;

    @BeforeEach
    void setUp() {
        // Create test fraud result
        fraudResult = new FraudDetectionResult();
        fraudResult.setTransactionId("TX-TEST-001");
        fraudResult.setAccountId("ACC-TEST-001");
        fraudResult.setFraudulent(true);
        Set<String> rules = new HashSet<>(Arrays.asList("Very High Amount Transaction"));
        fraudResult.setTriggeredRules(rules);
        fraudResult.setTransactionAmount(10000.0);
        fraudResult.setIpAddress("192.168.1.1");
        fraudResult.setDeviceId("DEV12345");
        fraudResult.setCountryCode("US");
        fraudResult.setDeviceType("MOBILE");
        fraudResult.setChannel("APP");
        fraudResult.setProcessedAt(LocalDateTime.now());
        
        // Configure mock service
        doNothing().when(notificationService).processNotification(any(FraudDetectionResult.class));
    }

    @Test
    void healthCheck_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/notifications/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification Service is running"));
    }

    @Test
    void receiveFraudResult_withValidResult_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/notifications/fraud-result")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fraudResult)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fraud notification received for transaction: TX-TEST-001"));
    }

    @Test
    void receiveFraudResult_withNonFraudResult_shouldReturnSuccess() throws Exception {
        fraudResult.setFraudulent(false);
        
        mockMvc.perform(post("/api/notifications/fraud-result")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fraudResult)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fraud notification received for transaction: TX-TEST-001"));
    }

    @Test
    void receiveFraudResult_withMissingTransactionId_shouldReturnBadRequest() throws Exception {
        fraudResult.setTransactionId(null);
        
        mockMvc.perform(post("/api/notifications/fraud-result")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fraudResult)))
                .andExpect(status().isBadRequest());
    }
} 