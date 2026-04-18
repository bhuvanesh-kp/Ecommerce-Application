package org.bhuvanesh.productservice.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.productservice.dto.ProductRequestDto;
import org.bhuvanesh.productservice.dto.ProductResponseDto;
import org.bhuvanesh.productservice.model.Category;
import org.bhuvanesh.productservice.model.Product;
import org.bhuvanesh.productservice.repository.ProductRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(@NonNull UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public ProductResponseDto createProduct(ProductRequestDto dto) {
        Product saved = productRepository.save(Product.builder()
                .name(dto.getName()).description(dto.getDescription())
                .price(dto.getPrice()).stockQuantity(dto.getStockQuantity())
                .imageUrl(dto.getImageUrl()).category(dto.getCategory())
                .build());

        return toResponseDto(saved);
    }

    public void deleteProduct(@NonNull UUID id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public void updateStock(@NonNull UUID id, int delta) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        int updated = product.getStockQuantity() + delta;
        if (updated < 0) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        product.setStockQuantity(updated);
        productRepository.save(product);
    }

    private ProductResponseDto toResponseDto(Product p) {
        return ProductResponseDto.builder()
                .name(p.getName()).description(p.getDescription())
                .price(p.getPrice()).stockQuantity(p.getStockQuantity())
                .imageUrl(p.getImageUrl()).category(p.getCategory())
                .build();
    }
}
