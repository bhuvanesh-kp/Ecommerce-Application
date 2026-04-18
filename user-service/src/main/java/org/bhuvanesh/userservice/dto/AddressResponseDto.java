package org.bhuvanesh.userservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class AddressResponseDto {

    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
