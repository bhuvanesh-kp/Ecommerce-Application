package org.bhuvanesh.orderservice.client.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private String productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
}
