package com.zzx.fraud.engine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "fraud_detection_results")
public class FraudDetectionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "is_fraudulent")
    private boolean isFraudulent;

    @Column(name = "triggered_rules")
    private String triggeredRules;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "transaction_amount")
    private Double transactionAmount;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "channel")
    private String channel;

    // Constructors
    public FraudDetectionResult() {
        this.processedAt = LocalDateTime.now();
    }

    public FraudDetectionResult(String transactionId, String accountId) {
        this();
        this.transactionId = transactionId;
        this.accountId = accountId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @JsonProperty("fraudulent")
    public boolean isFraudulent() {
        return isFraudulent;
    }

    @JsonProperty("fraudulent")
    public void setFraudulent(boolean fraudulent) {
        isFraudulent = fraudulent;
    }

    public String getTriggeredRules() {
        return triggeredRules;
    }

    public void setTriggeredRules(String triggeredRules) {
        this.triggeredRules = triggeredRules;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    // Helper methods
    public void addTriggeredRule(String ruleName) {
        if (triggeredRules == null) {
            triggeredRules = ruleName;
        } else {
            triggeredRules += "," + ruleName;
        }
    }

    @JsonIgnore
    public List<String> getTriggeredRulesList() {
        if (triggeredRules == null || triggeredRules.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(triggeredRules.split(",")));
    }
} 