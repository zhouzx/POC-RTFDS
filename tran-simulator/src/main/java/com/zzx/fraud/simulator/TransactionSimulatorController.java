package com.zzx.fraud.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zzx.fraud.simulator.service.TransactionSenderService;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/simulator")
public class TransactionSimulatorController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionSimulatorController.class);
    
    @Autowired
    private TransactionSenderService transactionSenderService;
    
    private final Random random = new Random();
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Transaction Simulator is running");
    }
    
    @PostMapping("/generate")
    public ResponseEntity<String> generateTransaction(
            @RequestParam(defaultValue = "1") int count,
            @RequestParam(defaultValue = "false") boolean fraudulent) {
        
        int sent = 0;
        for (int i = 0; i < count; i++) {
            TransactionRequest transaction = createTransaction(fraudulent);
            try {
                transactionSenderService.sendTransaction(transaction);
                sent++;
            } catch (Exception e) {
                logger.error("Failed to send transaction: {}", e.getMessage());
            }
        }
        
        return ResponseEntity.ok("Generated and sent " + sent + " transactions to RabbitMQ");
    }
    
    @PostMapping("/send")
    public ResponseEntity<String> sendCustomTransaction(@RequestBody TransactionRequest transaction) {
        try {
            transactionSenderService.sendTransaction(transaction);
            return ResponseEntity.ok("Transaction sent successfully to RabbitMQ");
        } catch (Exception e) {
            logger.error("Failed to send transaction: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to send transaction: " + e.getMessage());
        }
    }
    
    private TransactionRequest createTransaction(boolean fraudulent) {
        TransactionRequest transaction = new TransactionRequest();
        transaction.setTransactionId("TX" + UUID.randomUUID().toString().substring(0, 8));
        transaction.setAccountId("ACC" + (10000 + random.nextInt(90000)));
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setIpAddress(generateRandomIp());
        transaction.setDeviceId("DEV" + (10000 + random.nextInt(90000)));
        
        if (fraudulent) {
            // Generate suspicious transaction (high amount)
            transaction.setAmount(5000 + random.nextDouble() * 5000);
        } else {
            // Generate normal transaction
            transaction.setAmount(10 + random.nextDouble() * 990);
        }
        
        return transaction;
    }
    
    private String generateRandomIp() {
        return random.nextInt(256) + "." + 
               random.nextInt(256) + "." + 
               random.nextInt(256) + "." + 
               random.nextInt(256);
    }
} 