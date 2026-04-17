package org.bhuvanesh.ecommerce.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class AddressResponseDto {

    private UUID id;

    private String street;

    private String city;

    private String state;

    private String country;

    private String pincode;
}
