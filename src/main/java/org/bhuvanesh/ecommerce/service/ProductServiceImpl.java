package org.bhuvanesh.ecommerce.service;

import org.bhuvanesh.ecommerce.payload.ProductDTO;
import org.bhuvanesh.ecommerce.payload.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        return null;
    }

    @Override
    public ProductResponse getAllProducts() {
        return null;
    }

    @Override
    public ProductResponse getProductByCategory(Long categoryId) {
        return null;
    }

    @Override
    public ProductResponse getProductByKey(String keyword) {
        return null;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        return null;
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        return null;
    }

    @Override
    public ProductDTO updateImage(Long productId, MultipartFile image) {
        return null;
    }
}
