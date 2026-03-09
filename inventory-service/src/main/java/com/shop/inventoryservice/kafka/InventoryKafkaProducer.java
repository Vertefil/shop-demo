package com.shop.inventoryservice.kafka;

import com.shop.events.InventoryReservedEvent;
import com.shop.events.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInventoryReservedEvent(InventoryReservedEvent event) {
        kafkaTemplate.send("inventory.reserved", String.valueOf(event.getOrderId()), event);

        log.info("Событие отправлено в Kafka. Topic: {}, orderId: {}",
                "inventory.reserved",
                event.getOrderId()
        );

    }

    public void sendOrderCancelledEvent(OrderCancelledEvent event) {
        kafkaTemplate.send("order.cancelled", String.valueOf(event.getOrderId()), event);

        log.info("Событие отправлено в Kafka. Topic: {}, orderId: {}, reason: {}",
                "order.cancelled",
                event.getOrderId(),
                event.getReason()
        );
    }
}
