package org.bhuvanesh.ecommerce.dto;

import lombok.*;
import org.bhuvanesh.ecommerce.model.Category;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ProductResponseDto {

    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private String imageUrl;

    private Category category;
}
