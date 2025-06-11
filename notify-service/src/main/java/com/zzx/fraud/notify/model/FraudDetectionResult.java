package com.zzx.fraud.notify.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FraudDetectionResult {

    private Long id;
    private String transactionId;
    private String accountId;
    private boolean isFraudulent;
    private Set<String> triggeredRules = new HashSet<>();
    private Long processingTimeMs;
    private LocalDateTime processedAt;
    private Double transactionAmount;
    private String ipAddress;
    private String deviceId;
    private String countryCode;
    private String deviceType;
    private String channel;

    // Constructors
    public FraudDetectionResult() {
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

    @JsonProperty("isFraudulent")
    public boolean getIsFraudulent() {
        return isFraudulent;
    }

    @JsonProperty("isFraudulent")
    public void setIsFraudulent(boolean fraudulent) {
        isFraudulent = fraudulent;
    }

    public Set<String> getTriggeredRules() {
        return triggeredRules;
    }

    public void setTriggeredRules(Set<String> triggeredRules) {
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
    public List<String> getTriggeredRulesList() {
        if (triggeredRules == null || triggeredRules.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(triggeredRules);
    }

    @Override
    public String toString() {
        return "FraudDetectionResult{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", isFraudulent=" + isFraudulent +
                ", triggeredRules='" + triggeredRules + '\'' +
                ", processingTimeMs=" + processingTimeMs +
                ", processedAt=" + processedAt +
                ", transactionAmount=" + transactionAmount +
                ", ipAddress='" + ipAddress + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
} 