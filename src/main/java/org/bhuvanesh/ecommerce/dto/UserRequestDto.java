package org.bhuvanesh.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}
