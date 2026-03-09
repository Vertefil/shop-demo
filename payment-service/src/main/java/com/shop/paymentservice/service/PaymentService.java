package com.shop.paymentservice.service;

import com.shop.events.InventoryReservedEvent;
import com.shop.events.PaymentProcessedEvent;
import com.shop.paymentservice.dto.PaymentResponse;
import com.shop.paymentservice.entity.Payment;
import com.shop.paymentservice.kafka.PaymentKafkaProducer;
import com.shop.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentKafkaProducer kafkaProducer;
    private final Random random = new Random();

    @Transactional
    public void processPayment(InventoryReservedEvent event) {
        // симуляция оплаты 80% успеха
        boolean success = random.nextInt(10) < 8;

        Payment.PaymentStatus status = success ?
                Payment.PaymentStatus.SUCCESS :
                Payment.PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotalPrice())
                .status(status)
                .build();

        paymentRepository.save(payment);
        log.info("Платёж обработан. orderId: {}, статус: {}", event.getOrderId(), status);

        // Отправка события в Kafka
        PaymentProcessedEvent processedEvent = PaymentProcessedEvent.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotalPrice())
                .status(status.name())
                .build();

        kafkaProducer.sendProcessedEvent(processedEvent);
    }

    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }

}
