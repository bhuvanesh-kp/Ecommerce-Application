package org.bhuvanesh.orderservice.dto;

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
public class OrderRequestDto {

    @NotNull(message = "Shipping address ID is required")
    private UUID shippingAddressId;
}
