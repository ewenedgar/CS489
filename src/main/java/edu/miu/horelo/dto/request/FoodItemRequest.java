package edu.miu.horelo.dto.request;

import edu.miu.horelo.model.CombinedIngredients;
import edu.miu.horelo.model.Price;

public record FoodItemRequest(
        String name,
        String image,
        String spice_level,
        String cuisine_type,
        int calories,
        Price price,
        String description,
        String ingredients,
        Long subCategoryId,
        CombinedIngredients combinedIngredients,
        //@JsonProperty("openDaysAndHours")AvailableDaysAndHoursDTO availableDaysAndHoursDTO,
        String days_available,
        String visibility,
        Long estoreId,
        Integer userId
) {
}
