package com.zzx.fraud.integrator.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FraudDetectionResponse {
    private String transactionId;
    private boolean fraudulent;
    private List<String> triggeredRules;
    private LocalDateTime timestamp;
    private String message;
    private long processingTimeMs;

    // Constructors
    public FraudDetectionResponse() {
        this.triggeredRules = new ArrayList<>();
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isFraudulent() {
        return fraudulent;
    }

    public void setFraudulent(boolean fraudulent) {
        this.fraudulent = fraudulent;
    }

    public List<String> getTriggeredRules() {
        return triggeredRules;
    }

    public void setTriggeredRules(List<String> triggeredRules) {
        this.triggeredRules = triggeredRules;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    @Override
    public String toString() {
        return "FraudDetectionResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", fraudulent=" + fraudulent +
                ", triggeredRules=" + triggeredRules +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", processingTimeMs=" + processingTimeMs +
                '}';
    }
} 