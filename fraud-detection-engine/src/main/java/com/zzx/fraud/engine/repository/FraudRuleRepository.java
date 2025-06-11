package com.zzx.fraud.engine.repository;

import com.zzx.fraud.engine.model.FraudRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudRuleRepository extends JpaRepository<FraudRule, Long> {
    
    List<FraudRule> findByEnabledTrue();
    
    List<FraudRule> findByRuleTypeAndEnabledTrue(String ruleType);
} 