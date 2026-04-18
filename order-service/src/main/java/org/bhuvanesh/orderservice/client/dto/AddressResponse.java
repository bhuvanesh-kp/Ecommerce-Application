package org.bhuvanesh.orderservice.client.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
