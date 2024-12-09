package edu.miu.horelo.dto.request;

import edu.miu.horelo.model.Price;

public record ProductRequest(

        String name,
        String description,
        Long subCategoryId,
        int calories,
        Price price,
        Long estoreId,
        Integer userId

) {
}
