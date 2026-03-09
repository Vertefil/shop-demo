package com.shop.paymentservice.kafka;

import com.shop.events.InventoryReservedEvent;
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

    @KafkaListener(topics = "inventory.reserved", groupId = "payment-group")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Получено inventory.reserved для оплаты: orderId={}, amount={}",
                event.getOrderId(), event.getTotalPrice());
        paymentService.processPayment(event);
    }
}
