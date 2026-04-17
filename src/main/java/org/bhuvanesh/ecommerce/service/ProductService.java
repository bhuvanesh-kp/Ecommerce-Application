package org.bhuvanesh.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.bhuvanesh.ecommerce.dto.ProductRequestDto;
import org.bhuvanesh.ecommerce.dto.ProductResponseDto;
import org.bhuvanesh.ecommerce.model.Product;
import org.bhuvanesh.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        Product product = Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .stockQuantity(productRequestDto.getStockQuantity())
                .imageUrl(productRequestDto.getImageUrl())
                .category(productRequestDto.getCategory())
                .build();

        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.builder()
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .stockQuantity(savedProduct.getStockQuantity())
                .imageUrl(savedProduct.getImageUrl())
                .category(savedProduct.getCategory())
                .build();
    }
}
