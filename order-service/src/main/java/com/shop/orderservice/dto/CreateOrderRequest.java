package com.shop.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull(message = "userId обязателен")
    private Long userId;

    @NotNull(message = "productId обязателен")
    private Long productId;

    @NotNull
    @Min(value = 1, message = "Кол-во минимум 1")
    private Integer quantity;

    @NotNull(message = "Цена обязательна")
    private Double totalPrice;
}
