package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.request.CategoryRequest;
import edu.miu.horelo.dto.request.SubCategoryRequest;
import edu.miu.horelo.model.Category;
import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.SubCategory;
import edu.miu.horelo.repository.CategoryRepository;
import edu.miu.horelo.repository.EstoreRepository;
import edu.miu.horelo.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EstoreRepository estoreRepository;


    @Override
    public Category createCategory(CategoryRequest request) {
        Estore estore = estoreRepository.findByEstoreId(request.estoreId())
                .orElseThrow(()-> new ResourceNotFoundException("No estore found"));
        Category category = new Category(request.name(), estore);
        if(request.subCategoryList() != null) {
            for (SubCategoryRequest subCategoryRequest : request.subCategoryList()) {
                SubCategory subCategory = new SubCategory(subCategoryRequest.name(), category, estore);
                category.getSubCategoryList().add(subCategory);
            }
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findByCategoryId(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Estore estore = estoreRepository.findByEstoreId(request.estoreId())
                .orElseThrow(()-> new ResourceNotFoundException("No estore found"));
        category.setName(request.name());
        if(category.getSubCategoryList() != null) {
            category.getSubCategoryList().clear();
        }
        if(request.subCategoryList() != null) {
            for (SubCategoryRequest subCategoryRequest : request.subCategoryList()) {
                SubCategory subCategory = new SubCategory(subCategoryRequest.name(), category, estore);
                category.getSubCategoryList().add(subCategory);
            }
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findByCategoryId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
    @Override
    public List<Category> getCategoryByEstoreId(Long id) {
        return new ArrayList<>(categoryRepository.findCategoryByEstore_EstoreId(id));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteByCategoryId(id);
    }
}
