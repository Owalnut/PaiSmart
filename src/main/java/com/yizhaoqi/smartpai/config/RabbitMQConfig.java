package com.yizhaoqi.smartpai.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * RabbitMQ 配置（仿照 IntelliForum）
 * 文件处理任务：Exchange -> Queue -> Consumer；失败重试后进入死信队列
 */
@Configuration
public class RabbitMQConfig {

    public static final String FILE_PROCESSING_EXCHANGE = "file.processing.exchange";
    public static final String FILE_PROCESSING_QUEUE = "file.processing.queue";
    public static final String FILE_PROCESSING_ROUTING_KEY = "file.processing";
    public static final String FILE_PROCESSING_DLT_EXCHANGE = "file.processing.dlt.exchange";
    public static final String FILE_PROCESSING_DLT_QUEUE = "file.processing.dlt.queue";

    /**
     * JSON 消息转换器（与 IntelliForum 一致）
     */
    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }

    /**
     * 文件处理 DirectExchange
     */
    @Bean
    public DirectExchange fileProcessingExchange() {
        return new DirectExchange(FILE_PROCESSING_EXCHANGE, true, false);
    }

    /**
     * 死信 Exchange
     */
    @Bean
    public DirectExchange fileProcessingDltExchange() {
        return new DirectExchange(FILE_PROCESSING_DLT_EXCHANGE, true, false);
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue fileProcessingDltQueue() {
        return QueueBuilder.durable(FILE_PROCESSING_DLT_QUEUE).build();
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding fileProcessingDltBinding() {
        return BindingBuilder.bind(fileProcessingDltQueue())
                .to(fileProcessingDltExchange())
                .with(FILE_PROCESSING_ROUTING_KEY);
    }

    /**
     * 文件处理主队列，绑定死信
     */
    @Bean
    public Queue fileProcessingQueue() {
        return QueueBuilder.durable(FILE_PROCESSING_QUEUE)
                .withArgument("x-dead-letter-exchange", FILE_PROCESSING_DLT_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", FILE_PROCESSING_ROUTING_KEY)
                .build();
    }

    /**
     * 主队列绑定
     */
    @Bean
    public Binding fileProcessingBinding() {
        return BindingBuilder.bind(fileProcessingQueue())
                .to(fileProcessingExchange())
                .with(FILE_PROCESSING_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    /**
     * 监听器容器工厂：JSON 转换器 + 重试（失败后 nack 进死信）
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy backOff = new FixedBackOffPolicy();
        backOff.setBackOffPeriod(3000L); // 3 秒重试间隔（与原 Kafka 一致）
        retryTemplate.setBackOffPolicy(backOff);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);   // 最多 5 次（1 次 + 4 次重试，与 Kafka 一致）
        retryTemplate.setRetryPolicy(retryPolicy);
        factory.setRetryTemplate(retryTemplate);
        factory.setDefaultRequeueRejected(false); // 重试耗尽后不 requeue，进入死信
        return factory;
    }
}
