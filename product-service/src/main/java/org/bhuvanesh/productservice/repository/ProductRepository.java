package org.bhuvanesh.productservice.repository;

import org.bhuvanesh.productservice.model.Category;
import org.bhuvanesh.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategory(Category category);

    List<Product> findByNameContainingIgnoreCase(String name);
}
