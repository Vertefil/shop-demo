package com.shop.inventoryservice.kafka;

import com.shop.inventoryservice.event.OrderCreatedEvent;
import com.shop.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryKafkaConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order.created", groupId = "inventory-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Получено событие order.created: orderId={}, productId={}, quantity={}",
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity()
        );

        boolean reserved = inventoryService.reserveProduct(event);

        if (reserved) {
            log.info("Резервирование успешно для заказа: {}", event.getOrderId());
        } else {
            log.info("Резервирование не прошло для заказа: {}", event.getOrderId());
        }
    }
}
