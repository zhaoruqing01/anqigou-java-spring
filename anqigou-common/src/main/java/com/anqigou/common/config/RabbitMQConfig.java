package com.anqigou.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 队列配置
 */
@Configuration
public class RabbitMQConfig {
    
    // 订单相关
    public static final String ORDER_EXCHANGE = "anqigou.order.exchange";
    public static final String ORDER_QUEUE = "anqigou.order.queue";
    public static final String ORDER_ROUTING_KEY = "order.*";
    
    // 支付相关
    public static final String PAYMENT_EXCHANGE = "anqigou.payment.exchange";
    public static final String PAYMENT_QUEUE = "anqigou.payment.queue";
    public static final String PAYMENT_ROUTING_KEY = "payment.*";
    
    // 物流相关
    public static final String LOGISTICS_EXCHANGE = "anqigou.logistics.exchange";
    public static final String LOGISTICS_QUEUE = "anqigou.logistics.queue";
    public static final String LOGISTICS_ROUTING_KEY = "logistics.*";
    
    // 声明交换机
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange logisticsExchange() {
        return new DirectExchange(LOGISTICS_EXCHANGE, true, false);
    }
    
    // 声明队列
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }
    
    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }
    
    @Bean
    public Queue logisticsQueue() {
        return new Queue(LOGISTICS_QUEUE, true);
    }
    
    // 绑定队列到交换机
    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue)
                .to(orderExchange)
                .with(ORDER_ROUTING_KEY);
    }
    
    @Bean
    public Binding paymentBinding(Queue paymentQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentQueue)
                .to(paymentExchange)
                .with(PAYMENT_ROUTING_KEY);
    }
    
    @Bean
    public Binding logisticsBinding(Queue logisticsQueue, DirectExchange logisticsExchange) {
        return BindingBuilder.bind(logisticsQueue)
                .to(logisticsExchange)
                .with(LOGISTICS_ROUTING_KEY);
    }
}
