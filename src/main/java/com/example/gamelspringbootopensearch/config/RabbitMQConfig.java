package com.example.gamelspringbootopensearch.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    public static final String PRODUCT_REGISTRATION_QUEUE = "product.registration.queue";
    public static final String PRODUCT_REGISTRATION_DLQ = "product.registration.dlq";
    public static final String EXCHANGE = "product.registration.exchange";
    public static final String ROUTING_KEY = "product.registration.routingkey";

    @Bean
    public Queue productRegistrationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE);
        args.put("x-dead-letter-routing-key", "dlq");
        return new Queue(PRODUCT_REGISTRATION_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue productRegistrationDLQ() {
        return new Queue(PRODUCT_REGISTRATION_DLQ, true);
    }

    @Bean
    public TopicExchange productRegistrationExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingProductRegistrationQueue(Queue productRegistrationQueue, TopicExchange productRegistrationExchange) {
        return BindingBuilder.bind(productRegistrationQueue)
                .to(productRegistrationExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding bindingProductRegistrationDLQ(Queue productRegistrationDLQ, TopicExchange productRegistrationExchange) {
        return BindingBuilder.bind(productRegistrationDLQ)
                .to(productRegistrationExchange)
                .with("dlq");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, RetryOperationsInterceptor retryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(new MethodInterceptor[]{retryInterceptor});
        // 기본 메시지 컨버터를 사용합니다.
        return factory;
    }

    // 최초 시도 + 3회 재시도(총 4회 시도) 후 실패 시 메시지를 DLQ로 이동합니다.
    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(4)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }
}