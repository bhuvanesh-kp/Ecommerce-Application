package org.bhuvanesh.userservice.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.userservice.dto.*;
import org.bhuvanesh.userservice.model.*;
import org.bhuvanesh.userservice.repository.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RestTemplate restTemplate;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponseDto getUserById(@NonNull UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toResponseDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserRole(dto.getUserRole());

        User savedUser = userRepository.save(user);

        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            List<Address> addresses = dto.getAddresses().stream()
                    .map(a -> Address.builder()
                            .street(a.getStreet()).city(a.getCity())
                            .state(a.getState()).country(a.getCountry())
                            .pincode(a.getPincode()).user(savedUser)
                            .build())
                    .toList();
            addressRepository.saveAll(addresses);
        }

        return toResponseDto(savedUser);
    }

    public UserResponseDto updateUser(@NonNull UUID id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setUserRole(dto.getUserRole());

        return toResponseDto(userRepository.save(user));
    }

    public void clearCart(@NonNull UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public AddressResponseDto getAddressById(@NonNull UUID userId, @NonNull UUID addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));
        return AddressResponseDto.builder()
                .id(address.getId()).street(address.getStreet()).city(address.getCity())
                .state(address.getState()).country(address.getCountry()).pincode(address.getPincode())
                .build();
    }

    public void addAddress(@NonNull UUID userId, AddressRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        addressRepository.save(Address.builder()
                .street(dto.getStreet()).city(dto.getCity())
                .state(dto.getState()).country(dto.getCountry())
                .pincode(dto.getPincode()).user(user)
                .build());
    }

    @Transactional
    public CartResponseDto addToCart(@NonNull UUID userId, CartItemRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // fetch product details from product-service
        Map<?, ?> product = restTemplate.getForObject(
                "http://product-service/api/products/" + dto.getProductId(), Map.class);

        if (product == null) {
            throw new RuntimeException("Product not found with id: " + dto.getProductId());
        }

        String productName = (String) product.get("name");
        BigDecimal productPrice = new BigDecimal(product.get("price").toString());

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .productId(dto.getProductId())
                .productName(productName)
                .productPrice(productPrice)
                .quantity(dto.getQuantity())
                .build();

        cartItemRepository.save(cartItem);
        cart.getCartItems().add(cartItem);

        return toCartResponseDto(cart, user);
    }

    public CartResponseDto getCart(@NonNull UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));
        return toCartResponseDto(cart, cart.getUser());
    }

    @Transactional
    public CartResponseDto removeFromCart(@NonNull UUID userId, @NonNull UUID cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user id: " + userId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return toCartResponseDto(cart, cart.getUser());
    }

    private CartResponseDto toCartResponseDto(Cart cart, User user) {
        List<CartItemResponseDto> itemDtos = cart.getCartItems().stream()
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
                ? user.getAddresses().stream()
                        .map(a -> AddressResponseDto.builder()
                                .id(a.getId()).street(a.getStreet()).city(a.getCity())
                                .state(a.getState()).country(a.getCountry()).pincode(a.getPincode())
                                .build())
                        .toList()
                : new ArrayList<>();

        return UserResponseDto.builder()
                .firstName(user.getFirstName()).lastName(user.getLastName())
                .email(user.getEmail()).phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole()).addresses(addressDtos)
                .build();
    }
}
