package com.zzx.fraud.engine.repository;

import com.zzx.fraud.engine.model.FraudDetectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FraudDetectionResultRepository extends JpaRepository<FraudDetectionResult, Long> {
    
    Optional<FraudDetectionResult> findByTransactionId(String transactionId);
    
    List<FraudDetectionResult> findByAccountId(String accountId);
    
    List<FraudDetectionResult> findByIsFraudulentTrue();
    
    List<FraudDetectionResult> findByProcessedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<FraudDetectionResult> findByAccountIdAndProcessedAtBetween(String accountId, LocalDateTime start, LocalDateTime end);
    
    List<FraudDetectionResult> findByIsFraudulentTrueAndProcessedAtBetween(LocalDateTime start, LocalDateTime end);
} 