package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.CategoryRequest;
import edu.miu.horelo.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryRequest request);
    Category updateCategory(Long id, CategoryRequest request);
    Category getCategoryById(Long id);
    List<Category> getCategoryByEstoreId(Long id);
    void deleteCategory(Long id);
}
