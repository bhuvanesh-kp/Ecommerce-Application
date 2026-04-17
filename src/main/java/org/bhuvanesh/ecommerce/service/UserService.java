package org.bhuvanesh.ecommerce.service;

import lombok.RequiredArgsConstructor;
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
}
