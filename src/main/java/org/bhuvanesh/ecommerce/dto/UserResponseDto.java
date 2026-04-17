package org.bhuvanesh.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private String name;

    private String email;
}
