package org.bhuvanesh.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bhuvanesh.ecommerce.dto.OrderRequestDto;
import org.bhuvanesh.ecommerce.dto.OrderResponseDto;
import org.bhuvanesh.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/users/{id}/orders")
    public ResponseEntity<OrderResponseDto> placeOrder(@PathVariable @NonNull UUID id, @Valid @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(id, orderRequestDto));
    }

    @GetMapping("/api/users/{id}/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@PathVariable @NonNull UUID id) {
        return ResponseEntity.ok(orderService.getOrdersByUser(id));
    }

    @GetMapping("/api/users/{id}/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable @NonNull UUID id, @PathVariable @NonNull UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(id, orderId));
    }

    @PatchMapping("/api/users/{id}/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable @NonNull UUID id, @PathVariable @NonNull UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(id, orderId));
    }
}
