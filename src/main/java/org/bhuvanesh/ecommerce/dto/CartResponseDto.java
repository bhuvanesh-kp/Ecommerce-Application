package org.bhuvanesh.ecommerce.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CartResponseDto {

    private String userFullName;

    private List<CartItemResponseDto> cartItems;

    private BigDecimal cartTotal;
}
