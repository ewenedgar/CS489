package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.Price;
import edu.miu.horelo.model.SubCategory;

public record ProductResponse(
        Long productId,
        String name,
        String description,
        SubCategory subCategory,
        String image,
        int calories,
        Price price,
        EstoreResponse1 estore
) {
}
