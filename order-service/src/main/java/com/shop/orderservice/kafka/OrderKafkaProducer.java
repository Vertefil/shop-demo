package com.shop.orderservice.kafka;

import com.shop.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private static final String TOPIC = "order.created";
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.getOrderId()), event);
        log.info("Событие отправлено в Kafka. Topic: {}, orderId: {}", TOPIC, event.getOrderId());
    }
}
