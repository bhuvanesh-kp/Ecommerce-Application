package org.bhuvanesh.orderservice.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange
public interface ProductServiceClient {

    @PutExchange("/api/products/{id}/stock")
    void updateStock(@PathVariable String id, @RequestParam int delta);
}
