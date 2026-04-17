package org.bhuvanesh.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class OrderItemResponseDto {

    private String productName;

    private Integer quantity;

    private BigDecimal priceAtPurchase;

    private BigDecimal totalPrice;
}
