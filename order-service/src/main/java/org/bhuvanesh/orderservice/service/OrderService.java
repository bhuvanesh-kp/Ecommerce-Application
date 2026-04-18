package org.bhuvanesh.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.orderservice.client.ProductServiceClient;
import org.bhuvanesh.orderservice.client.UserServiceClient;
import org.bhuvanesh.orderservice.client.dto.AddressResponse;
import org.bhuvanesh.orderservice.client.dto.CartItemResponse;
import org.bhuvanesh.orderservice.client.dto.CartResponse;
import org.bhuvanesh.orderservice.client.dto.UserResponse;
import org.bhuvanesh.orderservice.dto.OrderItemResponseDto;
import org.bhuvanesh.orderservice.dto.OrderRequestDto;
import org.bhuvanesh.orderservice.dto.OrderResponseDto;
import org.bhuvanesh.orderservice.model.Order;
import org.bhuvanesh.orderservice.model.OrderItem;
import org.bhuvanesh.orderservice.model.OrderStatus;
import org.bhuvanesh.orderservice.repository.OrderRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public OrderResponseDto placeOrder(@NonNull UUID userId, OrderRequestDto dto) {
        UserResponse user = userServiceClient.getUserById(userId.toString());

        CartResponse cart = userServiceClient.getCart(userId.toString());
        List<CartItemResponse> cartItems = cart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cannot place order with an empty cart");
        }

        AddressResponse address = userServiceClient.getAddressById(
                userId.toString(), dto.getShippingAddressId().toString());

        String shippingAddress = address.getStreet() + ", " + address.getCity() + ", "
                + address.getState() + ", " + address.getCountry() + " - " + address.getPincode();

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = cartItems.stream().map(item -> OrderItem.builder()
                .order(order)
                .productId(UUID.fromString(item.getProductId()))
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .priceAtPurchase(item.getProductPrice())
                .build()
        ).toList();

        order.getOrderItems().addAll(orderItems);
        order.setTotalAmount(orderItems.stream()
                .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        orderRepository.save(order);

        orderItems.forEach(item ->
                productServiceClient.updateStock(item.getProductId().toString(), -item.getQuantity()));

        userServiceClient.clearCart(userId.toString());

        return toResponseDto(order, user.getFirstName() + " " + user.getLastName());
    }

    public List<OrderResponseDto> getOrdersByUser(@NonNull UUID userId) {
        UserResponse user = userServiceClient.getUserById(userId.toString());
        String userFullName = user.getFirstName() + " " + user.getLastName();

        return orderRepository.findByUserId(userId).stream()
                .map(order -> toResponseDto(order, userFullName))
                .toList();
    }

    public OrderResponseDto getOrderById(@NonNull UUID userId, @NonNull UUID orderId) {
        UserResponse user = userServiceClient.getUserById(userId.toString());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        return toResponseDto(order, user.getFirstName() + " " + user.getLastName());
    }

    @Transactional
    public OrderResponseDto cancelOrder(@NonNull UUID userId, @NonNull UUID orderId) {
        UserResponse user = userServiceClient.getUserById(userId.toString());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel an order with status: " + order.getStatus());
        }

        order.getOrderItems().forEach(item ->
                productServiceClient.updateStock(item.getProductId().toString(), item.getQuantity()));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return toResponseDto(order, user.getFirstName() + " " + user.getLastName());
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
