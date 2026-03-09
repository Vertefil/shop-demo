package com.shop.notificationservice.kafka;

import com.shop.events.OrderCancelledEvent;
import com.shop.events.OrderCreatedEvent;
import com.shop.events.PaymentProcessedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationKafkaConsumer {

    @KafkaListener(topics = "order.created", groupId = "notification-group")
    public void handeOrderCreated(OrderCreatedEvent event) {
        log.info("[УВЕДОМЛЕНИЕ] Новый заказ #{} от пользователя {}. Товар {}, кол-во: {}",
                event.getOrderId(),
                event.getUserId(),
                event.getProductId(),
                event.getQuantity()
        );
    }

    @KafkaListener(topics = "order.cancelled", groupId = "notification-group")
    public void handeOrderCancelled(OrderCancelledEvent event) {
        log.info("[УВЕДОМЛЕНИЕ] Заказ #{} ОТМЕНЁН! Причина: {}. Пользователь: {}",
                event.getOrderId(),
                event.getReason(),
                event.getUserId()
        );
    }

    @KafkaListener(topics = "payment.processed", groupId = "notification-group")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        if("SUCCESS".equals(event.getStatus())) {
            log.info("[УВЕДОМЛЕНИЕ] Оплата УСПЕШНА! Заказ #{}, сумма: {} руб. Пользователь: {}",
                    event.getOrderId(),
                    event.getAmount(),
                    event.getUserId()
            );
        } else {
            log.info("[УВЕДОМЛЕНИЕ] Оплата ОТКЛОНЕНА! Заказ #{}, сумма: {} руб. Пользователь: {}",
                    event.getOrderId(),
                    event.getAmount(),
                    event.getUserId()
            );
        }
    }

}
