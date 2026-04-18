package org.bhuvanesh.userservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CartItemResponseDto {

    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
}
