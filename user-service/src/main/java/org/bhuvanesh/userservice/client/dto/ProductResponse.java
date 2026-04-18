package org.bhuvanesh.userservice.client.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
}
