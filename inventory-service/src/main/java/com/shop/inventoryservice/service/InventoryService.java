package com.shop.inventoryservice.service;

import com.shop.events.OrderCreatedEvent;
import com.shop.inventoryservice.dto.CreateProductRequest;
import com.shop.inventoryservice.dto.ProductResponse;
import com.shop.inventoryservice.entity.Product;
import com.shop.inventoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();
        
        Product saved = productRepository.save(product);
        log.info("Создан товар {}", saved.getName());
        return toResponse(saved);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + id ));
        return toResponse(product);
    }

    @Transactional
    public boolean reserveProduct(OrderCreatedEvent event) {
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + event.getProductId()));
        if (product.getQuantity() < event.getQuantity()) {
            log.warn(
                    "Недостаточно товара. Заказ {}, нужно: {}, есть {}",
                    event.getOrderId(),
                    event.getQuantity(),
                    product.getQuantity()
            );
            return false;
        }

        product.setQuantity(product.getQuantity() - event.getQuantity());
        productRepository.save(product);
        log.info("Товар зарезервирован. Заказ {}, товар {}, остаток {}",
                event.getOrderId(),
                product.getName(),
                product.getQuantity()
        );

        return true;
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .build();
    }
}
