package com.shop.paymentservice.kafka;

import com.shop.events.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaProducer {

    private static final String TOPIC = "payment.processed";
    private final KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;

    public void sendProcessedEvent(PaymentProcessedEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.getOrderId()), event);
        log.info("Событие отправлено в Kafka. Topic: {}, orderId: {}, status: {}",
                TOPIC,
                event.getOrderId(),
                event.getStatus()
        );
    }
}
