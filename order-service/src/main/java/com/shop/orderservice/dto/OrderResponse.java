package com.shop.orderservice.dto;

import com.shop.orderservice.entity.Order;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
    private Order.OrderStatus status;
}
