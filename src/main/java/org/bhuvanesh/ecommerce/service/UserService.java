package org.bhuvanesh.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.ecommerce.dto.UserRequestDto;
import org.bhuvanesh.ecommerce.dto.UserResponseDto;
import org.bhuvanesh.ecommerce.model.User;
import org.bhuvanesh.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }
}
