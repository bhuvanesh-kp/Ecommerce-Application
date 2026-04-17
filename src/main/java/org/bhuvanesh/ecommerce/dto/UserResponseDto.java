package org.bhuvanesh.ecommerce.dto;

import lombok.*;
import org.bhuvanesh.ecommerce.model.UserRole;

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
}
