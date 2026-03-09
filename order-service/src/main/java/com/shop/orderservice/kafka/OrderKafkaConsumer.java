package com.shop.orderservice.kafka;

import com.shop.events.PaymentProcessedEvent;
import com.shop.orderservice.entity.Order;
import com.shop.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment.processed", groupId = "order-group")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Получен результат оплаты для заказа {}: {}",
                event.getOrderId(), event.getStatus()
        );

        Order.OrderStatus newStatus = "SUCCESS".
                equals(event.getStatus()) ?
                Order.OrderStatus.CONFIRMED :
                Order.OrderStatus.CANCELED;

        orderService.updateOrderStatus(event.getOrderId(), newStatus);
    }
}
