package com.zzx.fraud.integrator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zzx.fraud.integrator.model.EnhancedTransactionRequest;
import com.zzx.fraud.integrator.model.FraudDetectionResponse;
import com.zzx.fraud.integrator.model.TransactionRequest;

@Service
public class TransactionConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumerService.class);
    
    @Autowired
    private TransactionEnhancementService enhancementService;
    
    @Autowired
    private TransactionProducerService producerService;
    
    /**
     * Consume transaction messages from tran-simulator
     *
     * @param transaction Original transaction request
     */
    @RabbitListener(queues = "${rabbitmq.input.queue}")
    public void receiveTransaction(TransactionRequest transaction) {
        logger.info("Received transaction message: {}", transaction.getTransactionId());
        
        try {
            // Enhance transaction data
            EnhancedTransactionRequest enhancedTransaction = enhancementService.enhanceTransaction(transaction);
            
            // Send enhanced transaction data to fraud-detection-engine and get fraud detection result
            FraudDetectionResponse fraudResponse = producerService.sendToFraudDetectionEngine(enhancedTransaction);
            
            if (fraudResponse != null) {
                if (fraudResponse.isFraudulent()) {
                    logger.warn("Transaction marked as fraudulent: {}, Triggered Rules: {}",
                            transaction.getTransactionId(),
                            fraudResponse.getTriggeredRules());
                } else {
                    logger.info("Transaction normal: {}",
                            transaction.getTransactionId());
                }
            } else {
                logger.warn("No fraud detection result received: {}", transaction.getTransactionId());
            }
            
            logger.info("Transaction processing completed: {}", transaction.getTransactionId());
        } catch (Exception e) {
            logger.error("Error processing transaction: {}, Error: {}", transaction.getTransactionId(), e.getMessage(), e);
        }
    }
} 