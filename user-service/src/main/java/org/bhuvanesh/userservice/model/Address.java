package org.bhuvanesh.userservice.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Address {

    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
