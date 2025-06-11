package com.zzx.fraud.simulator.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.transactions:transactions-queue}")
    private String queueName;

    @Value("${rabbitmq.exchange:transactions-exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key:transactions}")
    private String routingKey;
    
    @Value("${spring.rabbitmq.host:rabbitmq-cn-0s64atwxg0z.cn-hangzhou.amqp-31.net.mq.amqp.aliyuncs.com}")
    private String host;
    
    @Value("${spring.rabbitmq.port:5672}")
    private int port;
    
    @Value("${spring.rabbitmq.username:MjpyYWJiaXRtcS1jbi0wczY0YXR3eGcwejpMVEFJNXQ3VnBEcDV1YndTdUV2RzNTTko=}")
    private String username;
    
    @Value("${spring.rabbitmq.password:QTEwOTBBOUJDQTNDQjNENTg2MUM3MTUxNUNDOUUwNUM4QjBBMDkxOToxNzQ5NTY0NTU5NTE5}")
    private String password;
    
    @Value("${spring.rabbitmq.virtual-host:trans}")
    private String virtualHost;

    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
} 