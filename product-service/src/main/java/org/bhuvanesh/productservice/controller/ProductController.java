package org.bhuvanesh.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bhuvanesh.productservice.dto.ProductRequestDto;
import org.bhuvanesh.productservice.dto.ProductResponseDto;
import org.bhuvanesh.productservice.model.Category;
import org.bhuvanesh.productservice.model.Product;
import org.bhuvanesh.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/internal/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable @NonNull UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/api/products/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @GetMapping("/api/products/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Category category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @PostMapping("/api/products")
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
    }

    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @NonNull UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/products/{id}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable @NonNull UUID id, @RequestParam int delta) {
        productService.updateStock(id, delta);
        return ResponseEntity.ok().build();
    }
}
