package com.zzx.fraud.engine.controller;

import com.zzx.fraud.engine.model.FraudRule;
import com.zzx.fraud.engine.repository.FraudRuleRepository;
import com.zzx.fraud.engine.service.FraudDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "*") // Enable CORS for all origins (in production, restrict this)
public class FraudRuleController {

    private static final Logger logger = LoggerFactory.getLogger(FraudRuleController.class);
    
    private final FraudRuleRepository ruleRepository;
    private final FraudDetectionService fraudDetectionService;
    
    @Autowired
    public FraudRuleController(FraudRuleRepository ruleRepository, FraudDetectionService fraudDetectionService) {
        this.ruleRepository = ruleRepository;
        this.fraudDetectionService = fraudDetectionService;
    }
    
    @GetMapping
    public ResponseEntity<List<FraudRule>> getAllRules() {
        logger.info("Fetching all fraud rules");
        List<FraudRule> rules = ruleRepository.findAll();
        return ResponseEntity.ok(rules);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FraudRule> getRuleById(@PathVariable Long id) {
        logger.info("Fetching fraud rule with id: {}", id);
        Optional<FraudRule> rule = ruleRepository.findById(id);
        return rule.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<FraudRule> createRule(@RequestBody FraudRule rule) {
        logger.info("Creating new fraud rule: {}", rule.getName());
        FraudRule savedRule = ruleRepository.save(rule);
        fraudDetectionService.refreshRules(); // Refresh rules after creation
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRule);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FraudRule> updateRule(@PathVariable Long id, @RequestBody FraudRule rule) {
        logger.info("Updating fraud rule with id: {}", id);
        
        if (!ruleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        rule.setId(id);
        FraudRule updatedRule = ruleRepository.save(rule);
        fraudDetectionService.refreshRules(); // Refresh rules after update
        return ResponseEntity.ok(updatedRule);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        logger.info("Deleting fraud rule with id: {}", id);
        
        if (!ruleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        ruleRepository.deleteById(id);
        fraudDetectionService.refreshRules(); // Refresh rules after deletion
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/enable")
    public ResponseEntity<FraudRule> enableRule(@PathVariable Long id) {
        logger.info("Enabling fraud rule with id: {}", id);
        return toggleRuleStatus(id, true);
    }
    
    @PatchMapping("/{id}/disable")
    public ResponseEntity<FraudRule> disableRule(@PathVariable Long id) {
        logger.info("Disabling fraud rule with id: {}", id);
        return toggleRuleStatus(id, false);
    }
    
    private ResponseEntity<FraudRule> toggleRuleStatus(Long id, boolean enabled) {
        Optional<FraudRule> ruleOpt = ruleRepository.findById(id);
        
        if (ruleOpt.isPresent()) {
            FraudRule rule = ruleOpt.get();
            rule.setEnabled(enabled);
            FraudRule updatedRule = ruleRepository.save(rule);
            fraudDetectionService.refreshRules(); // Refresh rules after status change
            return ResponseEntity.ok(updatedRule);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 