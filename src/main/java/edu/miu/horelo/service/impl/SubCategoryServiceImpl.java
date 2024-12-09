package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.request.SubCategoryRequest;
import edu.miu.horelo.model.Category;
import edu.miu.horelo.model.SubCategory;
import edu.miu.horelo.repository.SubCategoryRepository;
import edu.miu.horelo.repository.CategoryRepository;
import edu.miu.horelo.service.SubCategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubCategoryServiceImpl implements SubCategoryService {
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;



    @Override
    public SubCategory createSubCategory(Long categoryId, SubCategoryRequest request) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("CategoryRepository not found"));

        SubCategory subCategory = new SubCategory(request.name(), category, category.getEstore());
        return subCategoryRepository.save(subCategory);
    }

    @Override
    public SubCategory updateSubCategory(Long id, SubCategoryRequest request) {
        SubCategory subCategory = subCategoryRepository.findBySubCategoryId(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        Category category = categoryRepository.findByCategoryId(request.categoryId())
        .orElseThrow(()-> new ResourceNotFoundException("Category not found"));
        subCategory.setName(request.name());
        subCategory.setCategory(category);
        return subCategoryRepository.save(subCategory);
    }

    @Override
    public SubCategory getSubCategoryById(Long id) {
        return subCategoryRepository.findBySubCategoryId(id)
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
    }
    @Override
    public List<SubCategory> getSubCategoryByCategoryId(Long id) {
        return new ArrayList<>(subCategoryRepository.findSubCategoryByCategory_CategoryId(id));
    }

    @Override
    public void deleteSubCategory(Long id) {
        subCategoryRepository.deleteById(id);
    }
}
