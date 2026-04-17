package org.bhuvanesh.ecommerce.repository;

import org.bhuvanesh.ecommerce.model.Category;
import org.bhuvanesh.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategory(Category category);
}
