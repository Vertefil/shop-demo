package com.shop.paymentservice.kafka;

import com.shop.events.OrderCreatedEvent;
import com.shop.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = "order.created", groupId = "payment-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Получено событие order.created для оплаты: orderId{}, amount={}",
                event.getOrderId(),
                event.getTotalPrice()
        );
        paymentService.processPayment(event);
    }
}
