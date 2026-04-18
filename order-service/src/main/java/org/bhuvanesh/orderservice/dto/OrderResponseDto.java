package org.bhuvanesh.orderservice.dto;

import lombok.*;
import org.bhuvanesh.orderservice.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class OrderResponseDto {

    private String userFullName;
    private List<OrderItemResponseDto> orderItems;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private LocalDateTime createdAt;
}
