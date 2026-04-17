package org.bhuvanesh.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.ecommerce.dto.AddressRequestDto;
import org.bhuvanesh.ecommerce.dto.AddressResponseDto;
import org.bhuvanesh.ecommerce.dto.CartItemRequestDto;
import org.bhuvanesh.ecommerce.dto.CartItemResponseDto;
import org.bhuvanesh.ecommerce.dto.CartResponseDto;
import org.bhuvanesh.ecommerce.dto.UserRequestDto;
import org.bhuvanesh.ecommerce.dto.UserResponseDto;
import org.bhuvanesh.ecommerce.model.Address;
import org.bhuvanesh.ecommerce.model.Cart;
import org.bhuvanesh.ecommerce.model.CartItem;
import org.bhuvanesh.ecommerce.model.Product;
import org.bhuvanesh.ecommerce.model.User;
import org.bhuvanesh.ecommerce.repository.AddressRepository;
import org.bhuvanesh.ecommerce.repository.CartItemRepository;
import org.bhuvanesh.ecommerce.repository.CartRepository;
import org.bhuvanesh.ecommerce.repository.ProductRepository;
import org.bhuvanesh.ecommerce.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserResponseDto> getAllUsersPublic() {
        return userRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public UserResponseDto getUserById(@NonNull UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toResponseDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = new User();
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setUserRole(userRequestDto.getUserRole());

        User savedUser = userRepository.save(user);

        if (userRequestDto.getAddresses() != null && !userRequestDto.getAddresses().isEmpty()) {
            List<Address> addresses = userRequestDto.getAddresses().stream()
                    .map(dto -> Address.builder()
                            .street(dto.getStreet())
                            .city(dto.getCity())
                            .state(dto.getState())
                            .country(dto.getCountry())
                            .pincode(dto.getPincode())
                            .user(savedUser)
                            .build())
                    .toList();
            addressRepository.saveAll(addresses);
        }

        return toResponseDto(savedUser);
    }

    public UserResponseDto updateUser(@NonNull UUID id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setEmail(userRequestDto.getEmail());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setUserRole(userRequestDto.getUserRole());

        return toResponseDto(userRepository.save(user));
    }

    @Transactional
    public CartResponseDto addToCart(@NonNull UUID userId, CartItemRequestDto cartItemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Product product = productRepository.findById(cartItemRequestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + cartItemRequestDto.getProductId()));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(cartItemRequestDto.getQuantity())
                .build();

        cartItemRepository.save(cartItem);
        cart.getCartItems().add(cartItem);

        return toCartResponseDto(cart, user);
    }

    private CartResponseDto toCartResponseDto(Cart cart, User user) {
        List<CartItemResponseDto> itemDtos = cart.getCartItems().stream()
                .map(item -> CartItemResponseDto.builder()
                        .productName(item.getProduct().getName())
                        .productPrice(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getProduct().getPrice()
                                .multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        java.math.BigDecimal cartTotal = itemDtos.stream()
                .map(CartItemResponseDto::getTotalPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        return CartResponseDto.builder()
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .cartItems(itemDtos)
                .cartTotal(cartTotal)
                .build();
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

    public void addAddress(@NonNull UUID userId,AddressRequestDto addressRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Address address = Address.builder()
                .street(addressRequestDto.getStreet())
                .city(addressRequestDto.getCity())
                .state(addressRequestDto.getState())
                .country(addressRequestDto.getCountry())
                .pincode(addressRequestDto.getPincode())
                .user(user)
                .build();
        
        addressRepository.save(address);
    }

    private UserResponseDto toResponseDto(User user) {
        List<AddressResponseDto> addressDtos = user.getAddresses() != null
                ? user.getAddresses().stream()
                        .map(address -> AddressResponseDto.builder()
                                .id(address.getId())
                                .street(address.getStreet())
                                .city(address.getCity())
                                .state(address.getState())
                                .country(address.getCountry())
                                .pincode(address.getPincode())
                                .build())
                        .toList()
                : new ArrayList<>();

        return UserResponseDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .addresses(addressDtos)
                .build();
    }
}
