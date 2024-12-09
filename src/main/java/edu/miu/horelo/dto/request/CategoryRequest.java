package edu.miu.horelo.dto.request;


import java.util.List;

public record CategoryRequest(
        String name,
        Long estoreId,
        List<SubCategoryRequest> subCategoryList
) {
}
