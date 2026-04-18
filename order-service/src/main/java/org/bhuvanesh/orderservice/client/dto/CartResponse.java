package org.bhuvanesh.orderservice.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private String userFullName;
    private List<CartItemResponse> cartItems;
    private BigDecimal cartTotal;
}
