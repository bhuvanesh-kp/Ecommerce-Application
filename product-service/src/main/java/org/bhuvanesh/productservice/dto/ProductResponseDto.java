package org.bhuvanesh.productservice.dto;

import lombok.*;
import org.bhuvanesh.productservice.model.Category;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class ProductResponseDto {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private Category category;
}
