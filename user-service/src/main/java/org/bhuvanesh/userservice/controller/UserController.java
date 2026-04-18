package org.bhuvanesh.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bhuvanesh.userservice.dto.*;
import org.bhuvanesh.userservice.model.User;
import org.bhuvanesh.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/internal/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/api/users")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String id, @Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PostMapping("/api/users/{id}/addresses")
    public ResponseEntity<Void> addAddress(@PathVariable String id, @Valid @RequestBody AddressRequestDto dto) {
        userService.addAddress(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/users/{id}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddressById(@PathVariable String id, @PathVariable String addressId) {
        return ResponseEntity.ok(userService.getAddressById(id, addressId));
    }

    @GetMapping("/api/users/{id}/cart")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable String id) {
        return ResponseEntity.ok(userService.getCart(id));
    }

    @PostMapping("/api/users/{id}/cart")
    public ResponseEntity<CartResponseDto> addToCart(@PathVariable String id, @Valid @RequestBody CartItemRequestDto dto) {
        return ResponseEntity.ok(userService.addToCart(id, dto));
    }

    @DeleteMapping("/api/users/{id}/cart/{cartItemId}")
    public ResponseEntity<CartResponseDto> removeFromCart(@PathVariable String id, @PathVariable String cartItemId) {
        return ResponseEntity.ok(userService.removeFromCart(id, cartItemId));
    }

    @DeleteMapping("/api/users/{id}/cart")
    public ResponseEntity<Void> clearCart(@PathVariable String id) {
        userService.clearCart(id);
        return ResponseEntity.ok().build();
    }
}
