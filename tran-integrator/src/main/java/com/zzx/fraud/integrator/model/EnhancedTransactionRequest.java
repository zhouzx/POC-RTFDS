package com.zzx.fraud.integrator.model;

import java.time.LocalDateTime;

public class EnhancedTransactionRequest extends TransactionRequest {
    private String transactionId;
    private double amount;
    private String accountId;
    private String merchantId;
    private String location;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String deviceId;
    
    // Enhanced fields
    private String countryCode;
    private String deviceType;
    private String channel;
    private String currency;
    private Double exchangeRate;
    private LocalDateTime processingTimestamp;
    private String integrationId;
    private String merchantCategory;

    // Constructor to create enhanced transaction request from original transaction request
    public EnhancedTransactionRequest(TransactionRequest transaction) {
        // Copy fields from transaction
        this.setTransactionId(transaction.getTransactionId());
        this.setAccountId(transaction.getAccountId());
        this.setAmount(transaction.getAmount());
        this.setIpAddress(transaction.getIpAddress());
        this.setDeviceId(transaction.getDeviceId());
        this.setMerchantId(transaction.getMerchantId());
        this.setTimestamp(transaction.getTimestamp());
        
        // Set default values
        this.merchantCategory = "UNKNOWN";
        this.countryCode = "UNKNOWN";
        this.deviceType = "UNKNOWN";
        this.channel = "UNKNOWN";
        this.currency = "USD"; // Default currency
        this.exchangeRate = 1.0;
        this.processingTimestamp = LocalDateTime.now();
        this.integrationId = "INT-" + System.currentTimeMillis();
    }
    
    // Default constructor
    public EnhancedTransactionRequest() {
        this.processingTimestamp = LocalDateTime.now();
        this.integrationId = "INT-" + System.currentTimeMillis();
    }

    // Getters and setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }

    public void setMerchantCategory(String merchantCategory) {
        this.merchantCategory = merchantCategory;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public LocalDateTime getProcessingTimestamp() {
        return processingTimestamp;
    }

    public void setProcessingTimestamp(LocalDateTime processingTimestamp) {
        this.processingTimestamp = processingTimestamp;
    }

    public String getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(String integrationId) {
        this.integrationId = integrationId;
    }
} 