package com.zzx.fraud.engine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnhancedTransactionRequest {
    private String transactionId;
    private String accountId;
    private Double amount;
    private String currency;
    private String merchantName;
    private String merchantCategory;
    private String ipAddress;
    private LocalDateTime timestamp;
    private String deviceId;
    private String countryCode;
    private String deviceType;
    private String channel;
    
    // Added merchantId field to be compatible with data sent by tran-integrator service
    private String merchantId;

    // Constructors
    public EnhancedTransactionRequest() {
    }

    public EnhancedTransactionRequest(String transactionId, String accountId, Double amount, 
                                     String currency, String merchantName, String merchantCategory, 
                                     String ipAddress, LocalDateTime timestamp, String deviceId,
                                     String countryCode, String deviceType, String channel) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.countryCode = countryCode;
        this.deviceType = deviceType;
        this.channel = channel;
    }

    // Getters and Setters
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
        // If merchantName is null but merchantId is not null, use merchantId as merchantName
        if (this.merchantName == null && merchantId != null) {
            this.merchantName = merchantId;
        }
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }

    public void setMerchantCategory(String merchantCategory) {
        this.merchantCategory = merchantCategory;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public String toString() {
        return "EnhancedTransactionRequest{" +
                "transactionId='" + transactionId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", merchantCategory='" + merchantCategory + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", timestamp=" + timestamp +
                ", deviceId='" + deviceId + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
} 