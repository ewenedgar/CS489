package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.CombinedIngredients;
import edu.miu.horelo.model.Price;
import edu.miu.horelo.model.SubCategory;

public record FoodItemResponse(
        Long foodItemId,
        String name,
        String image,
        String spice_level,
        String cuisine_type,
        int calories,
        Price price,
        String description,
        String days_available,
        String visibility,
        String ingredients,
        SubCategory subCategory,
       // @JsonProperty("openDaysAndHours")AvailableDaysAndHours availableDaysAndHours,
        CombinedIngredients combinedIngredients,
        EstoreResponse1 estore,
        UserResponse1 user
) {
}
