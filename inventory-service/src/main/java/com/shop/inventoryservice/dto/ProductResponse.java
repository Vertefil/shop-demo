package com.shop.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private Integer quantity;
    private Double price;
}
