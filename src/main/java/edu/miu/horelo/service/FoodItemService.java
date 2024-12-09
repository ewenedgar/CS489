package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.FoodItemRequest;
import edu.miu.horelo.dto.request.AvailableDaysAndHoursDTO;
import edu.miu.horelo.dto.response.FoodItemResponse;
import edu.miu.horelo.dto.response.FoodItemResponse2;
import edu.miu.horelo.model.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FoodItemService {
    Page<FoodItemResponse> searchFoodItems(
            String foodItemName, String estoreName, String city,
            String street, String state, String productName,
            int page, int size
    );
    FoodItemResponse createFoodItem(FoodItemRequest request, Integer userId);
    List<FoodItemResponse> getAllFoodItems();
    Optional<FoodItemResponse> getFoodItemById(Long foodItemId);
    List<FoodItemResponse> getFoodItemsByEstore(Long estoreId);

    void deleteFoodItem(Long id);
    FoodItem updateOpenHours(Long foodItemId, AvailableDaysAndHoursDTO openDaysAndHoursDTO);
    FoodItemResponse updateFoodItem(Long id, FoodItemRequest foodItemRequest, Integer userId);
    FoodItemResponse updateFoodItemLogo(Long id, MultipartFile file);

    Page<FoodItemResponse> getEstoreFoodItems(Long estoreId,
                                              String itemType, int page, int size);

    Page<FoodItemResponse2> getFoodItemsWithoutEstore(int page, int size);
}
