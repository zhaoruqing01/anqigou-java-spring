package com.anqigou.order.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.anqigou.common.config.RabbitMQConfig;
import com.anqigou.common.event.OrderEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 订单事件监听器
 */
@Component
@Slf4j
public class OrderEventListener {
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderEvent(OrderEvent event) {
        log.info("接收订单事件 - 类型: {}, 订单ID: {}", event.getEventType(), event.getOrderId());
        
        switch (event.getEventType()) {
            case "created":
                handleOrderCreated(event);
                break;
            case "paid":
                handleOrderPaid(event);
                break;
            case "shipped":
                handleOrderShipped(event);
                break;
            case "delivered":
                handleOrderDelivered(event);
                break;
            case "cancelled":
                handleOrderCancelled(event);
                break;
            default:
                log.warn("未知的订单事件类型: {}", event.getEventType());
        }
    }
    
    private void handleOrderCreated(OrderEvent event) {
        log.info("订单已创建 - 订单ID: {}", event.getOrderId());
        // 实现：记录订单创建日志、发送确认通知等
        // TODO: 发送消息给用户、发邮件等通知
    }
    
    private void handleOrderPaid(OrderEvent event) {
        log.info("订单已支付 - 订单ID: {}", event.getOrderId());
        // 实现：更新订单状态、通知商家、扣库存等
        // TODO: 调用订单服务更新订单状态为"已支付"
        // TODO: 通知商家有新的待发货订单
    }
    
    private void handleOrderShipped(OrderEvent event) {
        log.info("订单已发货 - 订单ID: {}", event.getOrderId());
        // 实现：发送物流信息给用户、记录统计数据等
        // TODO: 发送物流通知给用户
        // TODO: 更新销售统计数据
    }
    
    private void handleOrderDelivered(OrderEvent event) {
        log.info("订单已送达 - 订单ID: {}", event.getOrderId());
        // 实现：自动确认收货或发送评价请求
        // TODO: 发送确认收货的通知
        // TODO: 开启自动确认计时
    }
    
    private void handleOrderCancelled(OrderEvent event) {
        log.info("订单已取消 - 订单ID: {}", event.getOrderId());
        // 实现：处理退款、库存回退、通知商家等
        // TODO: 调用支付服务处理退款
        // TODO: 调用库存服务回退商品库存
    }
}
