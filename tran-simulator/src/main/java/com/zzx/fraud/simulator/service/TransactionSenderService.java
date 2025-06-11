package com.zzx.fraud.simulator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zzx.fraud.simulator.TransactionRequest;

@Service
public class TransactionSenderService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionSenderService.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${rabbitmq.exchange:transactions-exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.routing-key:transactions}")
    private String routingKey;
    
    @Value("${spring.rabbitmq.mock:false}")
    private boolean mockEnabled;
    
    @Autowired
    public TransactionSenderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    /**
     * Sends a transaction to the RabbitMQ queue
     * 
     * @param transaction The transaction to send
     */
    public void sendTransaction(TransactionRequest transaction) {
        logger.info("Sending transaction to RabbitMQ: {}", transaction.getTransactionId());
        
        if (!mockEnabled) {
            try {
                rabbitTemplate.convertAndSend(exchangeName, routingKey, transaction);
                logger.info("Successfully sent transaction to RabbitMQ: {}", transaction.getTransactionId());
            } catch (Exception e) {
                logger.error("Failed to send transaction to RabbitMQ: {}, Error: {}", transaction.getTransactionId(), e.getMessage(), e);
                throw e;
            }
        } else {
            logger.info("Mock mode enabled. Transaction not actually sent: {}", transaction);
        }
    }
} 