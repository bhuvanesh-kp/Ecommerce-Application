package org.bhuvanesh.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.ecommerce.dto.UserRequestDto;
import org.bhuvanesh.ecommerce.dto.UserResponseDto;
import org.bhuvanesh.ecommerce.model.User;
import org.bhuvanesh.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return UserResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
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
