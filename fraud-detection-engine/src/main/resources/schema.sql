-- Drop tables if they exist
DROP TABLE IF EXISTS fraud_detection_results;
DROP TABLE IF EXISTS fraud_rules;

-- Create fraud_rules table
CREATE TABLE FRAUD_RULES (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    threshold_value DOUBLE,
    suspicious_pattern VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create fraud_detection_results table
CREATE TABLE FRAUD_DETECTION_RESULTS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    account_id VARCHAR(255),
    is_fraudulent BOOLEAN NOT NULL DEFAULT FALSE,
    triggered_rules VARCHAR(1000),
    processing_time_ms BIGINT,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_amount DOUBLE,
    ip_address VARCHAR(50),
    device_id VARCHAR(255),
    country_code VARCHAR(10),
    device_type VARCHAR(50),
    channel VARCHAR(50)
);

-- Create indexes
CREATE INDEX idx_transaction_id ON fraud_detection_results(transaction_id);
CREATE INDEX idx_account_id ON fraud_detection_results(account_id);
CREATE INDEX idx_is_fraudulent ON fraud_detection_results(is_fraudulent);
CREATE INDEX idx_processed_at ON fraud_detection_results(processed_at);
CREATE INDEX idx_rule_type ON fraud_rules(rule_type);
CREATE INDEX idx_enabled ON fraud_rules(enabled); 