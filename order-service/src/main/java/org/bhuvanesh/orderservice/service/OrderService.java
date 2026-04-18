package org.bhuvanesh.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.orderservice.dto.OrderItemResponseDto;
import org.bhuvanesh.orderservice.dto.OrderRequestDto;
import org.bhuvanesh.orderservice.dto.OrderResponseDto;
import org.bhuvanesh.orderservice.model.Order;
import org.bhuvanesh.orderservice.model.OrderItem;
import org.bhuvanesh.orderservice.model.OrderStatus;
import org.bhuvanesh.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${services.user-service}")
    private String userServiceUrl;

    @Value("${services.product-service}")
    private String productServiceUrl;

    @Transactional
    public OrderResponseDto placeOrder(@NonNull UUID userId, OrderRequestDto dto) {
        // fetch user and cart from user-service
        Map<?, ?> user = restTemplate.getForObject(userServiceUrl + "/api/users/" + userId, Map.class);
        if (user == null) throw new RuntimeException("User not found with id: " + userId);

        Map<?, ?> cart = restTemplate.getForObject(userServiceUrl + "/api/users/" + userId + "/cart", Map.class);
        if (cart == null) throw new RuntimeException("Cart not found for user id: " + userId);

        List<?> cartItems = (List<?>) cart.get("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cannot place order with an empty cart");
        }

        // fetch shipping address from user-service
        Map<?, ?> address = restTemplate.getForObject(
                userServiceUrl + "/api/users/" + userId + "/addresses/" + dto.getShippingAddressId(), Map.class);
        if (address == null) throw new RuntimeException("Address not found or does not belong to user");

        String shippingAddress = address.get("street") + ", " + address.get("city") + ", "
                + address.get("state") + ", " + address.get("country") + " - " + address.get("pincode");

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = cartItems.stream().map(ci -> {
            Map<?, ?> item = (Map<?, ?>) ci;
            BigDecimal price = new BigDecimal(item.get("productPrice").toString());
            Integer quantity = (Integer) item.get("quantity");

            return OrderItem.builder()
                    .order(order)
                    .productId(UUID.fromString(item.get("productId").toString()))
                    .productName((String) item.get("productName"))
                    .quantity(quantity)
                    .priceAtPurchase(price)
                    .build();
        }).toList();

        order.getOrderItems().addAll(orderItems);

        BigDecimal total = orderItems.stream()
                .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        orderRepository.save(order);

        // decrement stock in product-service for each item
        orderItems.forEach(item ->
                restTemplate.put(productServiceUrl + "/api/products/" + item.getProductId()
                        + "/stock?delta=" + (-item.getQuantity()), null));

        // clear cart in user-service
        restTemplate.delete(userServiceUrl + "/api/users/" + userId + "/cart");

        String userFullName = user.get("firstName") + " " + user.get("lastName");
        return toResponseDto(order, userFullName);
    }

    public List<OrderResponseDto> getOrdersByUser(@NonNull UUID userId) {
        Map<?, ?> user = restTemplate.getForObject(userServiceUrl + "/api/users/" + userId, Map.class);
        if (user == null) throw new RuntimeException("User not found with id: " + userId);
        String userFullName = user.get("firstName") + " " + user.get("lastName");

        return orderRepository.findByUserId(userId).stream()
                .map(order -> toResponseDto(order, userFullName))
                .toList();
    }

    public OrderResponseDto getOrderById(@NonNull UUID userId, @NonNull UUID orderId) {
        Map<?, ?> user = restTemplate.getForObject(userServiceUrl + "/api/users/" + userId, Map.class);
        if (user == null) throw new RuntimeException("User not found with id: " + userId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        return toResponseDto(order, user.get("firstName") + " " + user.get("lastName"));
    }

    @Transactional
    public OrderResponseDto cancelOrder(@NonNull UUID userId, @NonNull UUID orderId) {
        Map<?, ?> user = restTemplate.getForObject(userServiceUrl + "/api/users/" + userId, Map.class);
        if (user == null) throw new RuntimeException("User not found with id: " + userId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel an order with status: " + order.getStatus());
        }

        // restore stock in product-service
        order.getOrderItems().forEach(item ->
                restTemplate.put(productServiceUrl + "/api/products/" + item.getProductId()
                        + "/stock?delta=" + item.getQuantity(), null));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return toResponseDto(order, user.get("firstName") + " " + user.get("lastName"));
    }

    private OrderResponseDto toResponseDto(Order order, String userFullName) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(item -> OrderItemResponseDto.builder()
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .totalPrice(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponseDto.builder()
                .userFullName(userFullName)
                .orderItems(itemDtos)
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
