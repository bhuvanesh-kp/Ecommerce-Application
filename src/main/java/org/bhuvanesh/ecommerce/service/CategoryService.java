package org.bhuvanesh.ecommerce.service;



import org.bhuvanesh.ecommerce.model.Category;
import org.bhuvanesh.ecommerce.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories();
    void createCategory(Category category);

    String deleteCategory(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
