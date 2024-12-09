package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.Category;

public record SubCategoryResponse(
        String name,
        Category category
)

    {
    }