package org.bhuvanesh.userservice.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class CartItem {

    private String id;
    private String productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
}
