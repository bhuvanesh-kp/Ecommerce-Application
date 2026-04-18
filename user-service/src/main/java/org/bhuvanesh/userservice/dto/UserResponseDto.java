package org.bhuvanesh.userservice.dto;

import lombok.*;
import org.bhuvanesh.userservice.model.UserRole;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserResponseDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole userRole;
    private List<AddressResponseDto> addresses;
}
