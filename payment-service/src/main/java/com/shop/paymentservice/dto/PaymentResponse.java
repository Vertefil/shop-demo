package com.shop.paymentservice.dto;

import com.shop.paymentservice.entity.Payment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Long userId;
    private Double amount;
    private Payment.PaymentStatus status;
}
