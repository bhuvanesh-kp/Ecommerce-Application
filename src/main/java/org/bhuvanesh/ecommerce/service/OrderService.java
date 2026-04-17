package org.bhuvanesh.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.ecommerce.dto.OrderItemResponseDto;
import org.bhuvanesh.ecommerce.dto.OrderRequestDto;
import org.bhuvanesh.ecommerce.dto.OrderResponseDto;
import org.bhuvanesh.ecommerce.model.Address;
import org.bhuvanesh.ecommerce.model.Cart;
import org.bhuvanesh.ecommerce.model.Order;
import org.bhuvanesh.ecommerce.model.OrderItem;
import org.bhuvanesh.ecommerce.model.OrderStatus;
import org.bhuvanesh.ecommerce.model.Product;
import org.bhuvanesh.ecommerce.model.User;
import org.bhuvanesh.ecommerce.repository.AddressRepository;
import org.bhuvanesh.ecommerce.repository.CartRepository;
import org.bhuvanesh.ecommerce.repository.OrderRepository;
import org.bhuvanesh.ecommerce.repository.ProductRepository;
import org.bhuvanesh.ecommerce.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponseDto placeOrder(@NonNull UUID userId, OrderRequestDto orderRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with an empty cart");
        }

        // #5 - single query, no lazy-load of user
        UUID shippingAddressId = orderRequestDto.getShippingAddressId();
        Address shippingAddress = addressRepository.findByIdAndUserId(shippingAddressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));

        // #1 - validate stock for every item before touching anything
        cart.getCartItems().forEach(cartItem -> {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName()
                        + ". Available: " + product.getStockQuantity()
                        + ", Requested: " + cartItem.getQuantity());
            }
        });

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .priceAtPurchase(cartItem.getProduct().getPrice())
                        .build())
                .toList();

        order.getOrderItems().addAll(orderItems);

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        orderRepository.save(order);

        // #2 - decrement stock after order is persisted
        cart.getCartItems().forEach(cartItem -> {
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        });

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return toResponseDto(order, user);
    }

    public List<OrderResponseDto> getOrdersByUser(@NonNull UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return orderRepository.findByUserId(userId).stream()
                .map(order -> toResponseDto(order, order.getUser()))
                .toList();
    }

    // #4 - fetch single order by id for a user
    public OrderResponseDto getOrderById(@NonNull UUID userId, @NonNull UUID orderId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        return toResponseDto(order, order.getUser());
    }

    // #3 - cancel an order
    @Transactional
    public OrderResponseDto cancelOrder(@NonNull UUID userId, @NonNull UUID orderId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel an order with status: " + order.getStatus());
        }

        // restore stock on cancellation
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return toResponseDto(order, order.getUser());
    }

    private OrderResponseDto toResponseDto(Order order, User user) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(item -> OrderItemResponseDto.builder()
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .totalPrice(item.getPriceAtPurchase()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        Address addr = order.getShippingAddress();
        String shippingAddress = addr.getStreet() + ", " + addr.getCity() + ", "
                + addr.getState() + ", " + addr.getCountry() + " - " + addr.getPincode();

        return OrderResponseDto.builder()
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .orderItems(itemDtos)
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(shippingAddress)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
