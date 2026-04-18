package org.bhuvanesh.userservice.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.userservice.dto.*;
import org.bhuvanesh.userservice.model.*;
import org.bhuvanesh.userservice.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponseDto getUserById(@NonNull String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toResponseDto(user);
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .userRole(dto.getUserRole())
                .build();

        if (dto.getAddresses() != null) {
            List<Address> addresses = dto.getAddresses().stream()
                    .map(a -> Address.builder()
                            .id(UUID.randomUUID().toString())
                            .street(a.getStreet()).city(a.getCity())
                            .state(a.getState()).country(a.getCountry())
                            .pincode(a.getPincode())
                            .build())
                    .toList();
            user.setAddresses(addresses);
        }

        return toResponseDto(userRepository.save(user));
    }

    public UserResponseDto updateUser(@NonNull String id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserRole(dto.getUserRole());

        return toResponseDto(userRepository.save(user));
    }

    public void addAddress(@NonNull String userId, AddressRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.getAddresses().add(Address.builder()
                .id(UUID.randomUUID().toString())
                .street(dto.getStreet()).city(dto.getCity())
                .state(dto.getState()).country(dto.getCountry())
                .pincode(dto.getPincode())
                .build());

        userRepository.save(user);
    }

    public AddressResponseDto getAddressById(@NonNull String userId, @NonNull String addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Address address = user.getAddresses().stream()
                .filter(a -> addressId.equals(a.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));

        return toAddressResponseDto(address);
    }

    public CartResponseDto addToCart(@NonNull String userId, CartItemRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Map<?, ?> product = restTemplate.getForObject(
                "http://product-service/api/products/" + dto.getProductId(), Map.class);

        if (product == null) {
            throw new RuntimeException("Product not found with id: " + dto.getProductId());
        }

        String productName = (String) product.get("name");
        BigDecimal productPrice = new BigDecimal(product.get("price").toString());

        CartItem cartItem = CartItem.builder()
                .id(UUID.randomUUID().toString())
                .productId(dto.getProductId())
                .productName(productName)
                .productPrice(productPrice)
                .quantity(dto.getQuantity())
                .build();

        user.getCartItems().add(cartItem);
        userRepository.save(user);

        return toCartResponseDto(user);
    }

    public CartResponseDto getCart(@NonNull String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return toCartResponseDto(user);
    }

    public CartResponseDto removeFromCart(@NonNull String userId, @NonNull String cartItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean removed = user.getCartItems().removeIf(item -> cartItemId.equals(item.getId()));
        if (!removed) {
            throw new RuntimeException("Cart item not found with id: " + cartItemId);
        }

        userRepository.save(user);
        return toCartResponseDto(user);
    }

    public void clearCart(@NonNull String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.getCartItems().clear();
        userRepository.save(user);
    }

    private CartResponseDto toCartResponseDto(User user) {
        List<CartItemResponseDto> itemDtos = user.getCartItems().stream()
                .map(item -> CartItemResponseDto.builder()
                        .productName(item.getProductName())
                        .productPrice(item.getProductPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        BigDecimal cartTotal = itemDtos.stream()
                .map(CartItemResponseDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponseDto.builder()
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .cartItems(itemDtos)
                .cartTotal(cartTotal)
                .build();
    }

    private UserResponseDto toResponseDto(User user) {
        List<AddressResponseDto> addressDtos = user.getAddresses() != null
                ? user.getAddresses().stream().map(this::toAddressResponseDto).toList()
                : new ArrayList<>();

        return UserResponseDto.builder()
                .firstName(user.getFirstName()).lastName(user.getLastName())
                .email(user.getEmail()).phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole()).addresses(addressDtos)
                .build();
    }

    private AddressResponseDto toAddressResponseDto(Address a) {
        return AddressResponseDto.builder()
                .id(a.getId()).street(a.getStreet()).city(a.getCity())
                .state(a.getState()).country(a.getCountry()).pincode(a.getPincode())
                .build();
    }
}
