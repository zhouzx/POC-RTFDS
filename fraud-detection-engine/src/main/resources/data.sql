-- Insert fraud detection rules
INSERT INTO fraud_rules (name, description, rule_type, threshold_value, suspicious_pattern, enabled, created_at, updated_at)
VALUES
    ('Very High Amount Transaction', 'Flags transactions with extremely high amounts', 'AMOUNT_THRESHOLD', 5000.0, NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Suspicious Account Pattern', 'Flags transactions from accounts with suspicious patterns', 'SUSPICIOUS_ACCOUNT', NULL, 'FRAUD-|BLACKLIST-', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);