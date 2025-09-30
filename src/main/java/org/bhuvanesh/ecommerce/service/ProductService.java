package org.bhuvanesh.ecommerce.service;

import jakarta.validation.Valid;
import org.bhuvanesh.ecommerce.payload.ProductDTO;
import org.bhuvanesh.ecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductDTO addProduct(@Valid ProductDTO productDTO, Long categoryId);

    ProductResponse getAllProducts();

    ProductResponse getProductByCategory(Long categoryId);

    ProductResponse getProductByKey(String keyword);

    ProductDTO updateProduct(@Valid ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateImage(Long productId, MultipartFile image);
}
