package org.bhuvanesh.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bhuvanesh.ecommerce.dto.AddressRequestDto;
import org.bhuvanesh.ecommerce.dto.CartItemRequestDto;
import org.bhuvanesh.ecommerce.dto.CartResponseDto;
import org.bhuvanesh.ecommerce.dto.UserRequestDto;
import org.bhuvanesh.ecommerce.dto.UserResponseDto;
import org.bhuvanesh.ecommerce.model.User;
import org.bhuvanesh.ecommerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/internal/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable @NonNull UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable @NonNull UUID id, @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.updateUser(id, userRequestDto));
    }

    @PostMapping("/api/users")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDto));
    }

    @PostMapping("/api/users/{id}/addresses")
    public ResponseEntity<Void> addAddress(@PathVariable @NonNull UUID id, @Valid @RequestBody AddressRequestDto addressRequestDto) {
        userService.addAddress(id, addressRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/users/{id}/cart")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable @NonNull UUID id) {
        return ResponseEntity.ok(userService.getCart(id));
    }

    @PostMapping("/api/users/{id}/cart")
    public ResponseEntity<CartResponseDto> addToCart(@PathVariable @NonNull UUID id, @Valid @RequestBody CartItemRequestDto cartItemRequestDto) {
        return ResponseEntity.ok(userService.addToCart(id, cartItemRequestDto));
    }

    @DeleteMapping("/api/users/{id}/cart/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeFromCart(@PathVariable @NonNull UUID id, @PathVariable @NonNull UUID cartItemId) {
        return ResponseEntity.ok(userService.removeFromCart(id, cartItemId));
    }
}
