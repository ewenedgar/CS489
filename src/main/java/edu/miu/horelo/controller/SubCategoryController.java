package edu.miu.horelo.controller;

import edu.miu.horelo.dto.request.SubCategoryRequest;
import edu.miu.horelo.model.SubCategory;
import edu.miu.horelo.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/auth/subcategory")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @PostMapping("/category/{categoryId}")
    public ResponseEntity<SubCategory> createSubCategory(
            @PathVariable Long categoryId,
            @RequestBody SubCategoryRequest request) {
        SubCategory createdSubCategory = subCategoryService.createSubCategory(categoryId, request);
        return ResponseEntity.ok(createdSubCategory);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubCategory> updateSubCategory(
            @PathVariable Long id,
            @RequestBody SubCategoryRequest request) {
        SubCategory updatedSubCategory = subCategoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(updatedSubCategory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategory> getSubCategoryById(@PathVariable Long id) {
        SubCategory subCategory = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(subCategory);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SubCategory>> getSubCategoriesByCategoryId(@PathVariable Long categoryId) {
        List<SubCategory> subCategories = subCategoryService.getSubCategoryByCategoryId(categoryId);
        return ResponseEntity.ok(subCategories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }
}