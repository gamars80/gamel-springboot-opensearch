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

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String PRODUCT_REGISTRATION_QUEUE = "product.registration.queue";
    public static final String PRODUCT_REGISTRATION_DLQ = "product.registration.dlq";
    public static final String EXCHANGE = "product.registration.exchange";
    public static final String ROUTING_KEY = "product.registration.routingkey";
    public static final String PRODUCT_UPDATE_QUEUE = "product.update.queue";
    public static final String PRODUCT_UPDATE_DLQ = "product.update.dlq";
    public static final String UPDATE_ROUTING_KEY = "product.update.routingkey";
    public static final String UPDATE_EXCHANGE = "product.update.exchange";

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
    public Queue productUpdateQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", UPDATE_EXCHANGE);
        args.put("x-dead-letter-routing-key", "update-dlq");
        return new Queue(PRODUCT_UPDATE_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue productUpdateDLQ() {
        return new Queue(PRODUCT_UPDATE_DLQ, true);
    }

    @Bean
    public TopicExchange productUpdateExchange() {
        return new TopicExchange(UPDATE_EXCHANGE);
    }

    @Bean
    public Binding bindingProductUpdateQueue(Queue productUpdateQueue, TopicExchange productUpdateExchange) {
        return BindingBuilder.bind(productUpdateQueue)
                .to(productUpdateExchange)
                .with(UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding bindingProductUpdateDLQ(Queue productUpdateDLQ, TopicExchange productUpdateExchange) {
        return BindingBuilder.bind(productUpdateDLQ)
                .to(productUpdateExchange)
                .with("update-dlq");
    }


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            RetryOperationsInterceptor retryInterceptor,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(new MethodInterceptor[]{retryInterceptor});
        // Jackson2JsonMessageConverter를 사용하도록 설정
        factory.setMessageConverter(jsonMessageConverter);
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

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}