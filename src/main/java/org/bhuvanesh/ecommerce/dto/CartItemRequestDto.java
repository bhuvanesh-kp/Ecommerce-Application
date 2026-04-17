package org.bhuvanesh.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CartItemRequestDto {

    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
