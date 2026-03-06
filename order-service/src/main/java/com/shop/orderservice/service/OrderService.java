package com.shop.orderservice.service;

import com.shop.orderservice.dto.CreateOrderRequest;
import com.shop.orderservice.dto.OrderResponse;
import com.shop.orderservice.entity.Order;
import com.shop.orderservice.event.OrderCreatedEvent;
import com.shop.orderservice.kafka.OrderKafkaProducer;
import com.shop.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderKafkaProducer kafkaProducer;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Сохраняем заказ и вешаем статус PENDING (ожидание)
        Order order = Order.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalPrice(request.getTotalPrice())
                .status(Order.OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);
        log.info("Заказ создан с статусом PENDING: orderId={} ", saved.getId());

        // Отправляем событие в Kafka
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(saved.getId())
                .productId(saved.getProductId())
                .userId(saved.getUserId())
                .quantity(saved.getQuantity())
                .totalPrice(saved.getTotalPrice())
                .build();

        kafkaProducer.sendOrderCreatedEvent(event);

        return toResponse(saved);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + id));
        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));
        order.setStatus(status);

        Order saved = orderRepository.save(order);
        log.info("Статус заказа {} обновлён на {}", orderId, status);
        return  toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .build();
    }
}
