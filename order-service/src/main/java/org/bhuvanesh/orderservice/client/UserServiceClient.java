package org.bhuvanesh.orderservice.client;

import org.bhuvanesh.orderservice.client.dto.AddressResponse;
import org.bhuvanesh.orderservice.client.dto.CartResponse;
import org.bhuvanesh.orderservice.client.dto.UserResponse;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface UserServiceClient {

    @GetExchange("/api/users/{id}")
    UserResponse getUserById(@org.springframework.web.bind.annotation.PathVariable String id);

    @GetExchange("/api/users/{id}/cart")
    CartResponse getCart(@org.springframework.web.bind.annotation.PathVariable String id);

    @GetExchange("/api/users/{id}/addresses/{addressId}")
    AddressResponse getAddressById(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @org.springframework.web.bind.annotation.PathVariable String addressId);

    @DeleteExchange("/api/users/{id}/cart")
    void clearCart(@org.springframework.web.bind.annotation.PathVariable String id);
}
