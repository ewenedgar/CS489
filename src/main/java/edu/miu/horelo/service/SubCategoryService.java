package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.SubCategoryRequest;
import edu.miu.horelo.model.SubCategory;

import java.util.List;

public interface SubCategoryService {
    SubCategory createSubCategory(Long categoryId, SubCategoryRequest request);
    SubCategory updateSubCategory(Long id, SubCategoryRequest request);
    SubCategory getSubCategoryById(Long id);
    List<SubCategory> getSubCategoryByCategoryId(Long id);
    void deleteSubCategory(Long id);
}
