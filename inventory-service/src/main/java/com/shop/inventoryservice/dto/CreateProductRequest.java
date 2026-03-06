package com.shop.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Название обязательно")
    private String name;

    @NotNull
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer quantity;

    @NotNull
    @Min(value = 0, message = "Цена не может быть отрицательным")
    private Double price;
}
