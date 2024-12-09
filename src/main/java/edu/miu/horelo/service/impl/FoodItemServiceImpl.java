package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.FileDTO;
import edu.miu.horelo.dto.SavedFileDTO;
import edu.miu.horelo.dto.request.AvailableDaysAndHoursDTO;
import edu.miu.horelo.dto.request.FoodItemRequest;
import edu.miu.horelo.dto.response.FoodItemResponse;
import edu.miu.horelo.dto.response.EstoreResponse1;
import edu.miu.horelo.dto.response.FoodItemResponse2;
import edu.miu.horelo.dto.response.UserResponse1;
import edu.miu.horelo.model.*;
import edu.miu.horelo.repository.*;
import edu.miu.horelo.service.FileManagerService;
import edu.miu.horelo.service.FoodItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodItemRepository foodItemRepository;

    private final EstoreRepository estoreRepository;
    private final UserRepository userRepository; // Inject UserRepository
    private final UserEstoreRoleRepository userEstoreRoleRepository;
    private final FileManagerService fileManagerService;
    private final SubCategoryRepository subCategoryRepository;

    @Override
    public Page<FoodItemResponse> searchFoodItems(
            String foodItemName, String estoreName, String city,
            String street, String state, String productName,
            int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return foodItemRepository.searchByCriteria(
                foodItemName, estoreName, city, street, state, productName, pageable
        ).map(this::mapToResponse);
    }
    @Override
    @Transactional
    //@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_OWNER') or hasRole('ROLE_MANAGER')")
    public FoodItemResponse createFoodItem(FoodItemRequest foodItemRequest, Integer userId) {
        // Retrieve estore and user from their respective repositories
        Estore estore = estoreRepository.findByEstoreId(foodItemRequest.estoreId())
                .orElseThrow(() -> new EntityNotFoundException("Estore not found"));
        User user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        SubCategory subCategory = subCategoryRepository.findBySubCategoryId(foodItemRequest.subCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("No subcategory found"));


        // Create a new FoodItem entity
        FoodItem foodItem = getFoodItem(foodItemRequest, userId);
        //System.out.println("OpenPeriodsByDay: " + foodItem.getAvailableDaysAndHours().getOpenPeriodsByDay());
        // Ensure AvailableDaysAndHours is initialized before updating
        /*if (foodItemRequest.availableDaysAndHoursDTO() != null) {
            if (foodItem.getAvailableDaysAndHours() == null) {
                // Initialize AvailableDaysAndHours if not present
                foodItem.setAvailableDaysAndHours(new AvailableDaysAndHours());
            }
            System.out.println("OpenPeriodsByDay: " + foodItem.getAvailableDaysAndHours().getOpenPeriodsByDay());
            // Update AvailableDaysAndHours from the DTO
            updateAvailableDaysAndHours(foodItem, foodItemRequest.availableDaysAndHoursDTO());
        }*/
        foodItem.setSubCategory(subCategory);
        foodItem.setEstore(estore);
        foodItem.setEstoreId(foodItem.getEstoreId());
        // Save the FoodItem to the database
        FoodItem savedFoodItem = foodItemRepository.save(foodItem);

        // Map the saved FoodItem entity to FoodItemResponse DTO and return it
        return new FoodItemResponse(
                savedFoodItem.getFoodItemId(),
                savedFoodItem.getName(),
                savedFoodItem.getImage(),
                savedFoodItem.getSpice_level(),
                savedFoodItem.getCuisine_type(),
                savedFoodItem.getCalories(),
                savedFoodItem.getPrice(),
                savedFoodItem.getDescription(),
                savedFoodItem.getDays_available(),
                savedFoodItem.getVisibility(),
                savedFoodItem.getIngredients(),
                savedFoodItem.getSubCategory(),
                //foodItem.getAvailableDaysAndHours(),
                savedFoodItem.getCombinedIngredients(),
                new EstoreResponse1(estore.getEstoreId(), estore.getName()),  // Assuming EstoreResponse1 has these fields
                new UserResponse1( user.getUserId())        // Assuming UserResponse1 has these fields
        );

    }



    private static FoodItem getFoodItem(FoodItemRequest foodItemRequest, Integer userId) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(foodItemRequest.name());
        foodItem.setCalories(foodItemRequest.calories());
        foodItem.setCuisine_type(foodItemRequest.cuisine_type());
        foodItem.setSpice_level(foodItemRequest.spice_level());
        foodItem.setDays_available(foodItemRequest.days_available());
        foodItem.setIngredients(foodItemRequest.ingredients());
        foodItem.setCombinedIngredients(foodItemRequest.combinedIngredients());
        foodItem.setImage(foodItemRequest.image());
        foodItem.setPrice(foodItemRequest.price());
        foodItem.setDescription(foodItemRequest.description());
        foodItem.setVisibility(foodItemRequest.visibility());
        foodItem.setEstoreId(foodItemRequest.estoreId());
        foodItem.setUserId(userId);
        return foodItem;
    }

    @Override
    @Transactional
    public FoodItemResponse updateFoodItem(Long foodItemId, FoodItemRequest request, Integer userId) {
        FoodItem existingFoodItem = foodItemRepository.findByFoodItemId(foodItemId)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));

        Estore estore = estoreRepository.findByEstoreId(request.estoreId())
                .orElseThrow(() -> new IllegalArgumentException("Estore not found"));
        SubCategory subCategory = subCategoryRepository.findBySubCategoryId(request.subCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("No subcategory found"));
        existingFoodItem.setName(request.name());
        existingFoodItem.setSubCategory(subCategory);
        existingFoodItem.setCuisine_type(request.cuisine_type());
        existingFoodItem.setSpice_level(request.spice_level());
        existingFoodItem.setDays_available(request.days_available());
        existingFoodItem.setIngredients(request.ingredients());
        existingFoodItem.setImage(request.image());
        existingFoodItem.setPrice(request.price());
        existingFoodItem.setDescription(request.description());
        existingFoodItem.setVisibility(request.visibility());
        existingFoodItem.setEstoreId(estore.getEstoreId());
        existingFoodItem.setEstore(estore);
        existingFoodItem.setUserId(userId); // Update the User entity
      /*  // Update OpenDaysAndHours if provided
        if (request.availableDaysAndHoursDTO() != null) {
            updateAvailableDaysAndHours(existingFoodItem, request.availableDaysAndHoursDTO());
        }*/

        FoodItem updatedFoodItem = foodItemRepository.save(existingFoodItem);

        return mapToResponse1(updatedFoodItem);
    }
    @Override
    @Transactional
    public FoodItemResponse updateFoodItemLogo(Long id, MultipartFile file){
        FoodItem pdt = foodItemRepository.findByFoodItemId(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found with id " + id));
        // Check if a file is present and handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                // Create a FileDTO instance
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFile(file); // Set the MultipartFile
                fileDTO.setFileName(file.getOriginalFilename()); // Set the original file name

                // Get the existing avatar filename for deletion
                String oldAvatar = pdt.getImage();

                // Call fileManagerService to upload the file and delete the old one
                SavedFileDTO savedFile = fileManagerService.uploadFile(fileDTO, "food-item", oldAvatar); // null for default folder

                // Set the new avatar URL based on the uploaded file
                pdt.setImage(savedFile.getGeneratedFileName()); // Assuming you store the file URL or name
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file", e);
            }
        }
        // Save the updated store back to the repository
        var savedProduct = foodItemRepository.save(pdt);

        return mapToResponse1(savedProduct);
    }

    @Override
    public Page<FoodItemResponse> getEstoreFoodItems(Long estoreId, String itemType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Query based on itemType
        Page<FoodItem> foodItems;
        if (itemType != null && !itemType.isBlank()) {
            foodItems = foodItemRepository.findFoodItemsByEstore_EstoreId(estoreId, pageable);
        } else {
            foodItems = foodItemRepository.findFoodItemsByEstore_EstoreId(estoreId, pageable);
        }

        // Map FoodItem entities to FoodItemResponse DTOs
        return foodItems.map(this::mapToResponse1);
    }

    @Override
    public Page<FoodItemResponse2> getFoodItemsWithoutEstore(int page, int size) {
        // Define pagination
        Pageable pageable = PageRequest.of(page, size);

        // Query food items without an associated estore
        Page<FoodItem> foodItems = foodItemRepository.findAll(pageable);

        // Map the results to FoodItemResponse DTOs
        return foodItems.map(this::mapToResponse2);
    }

    @Override
    @Transactional
    public void deleteFoodItem(Long foodItemId) {
        if (!foodItemRepository.existsById(foodItemId)) {
            throw new IllegalArgumentException("Food item not found");
        }
        foodItemRepository.deleteById(foodItemId);
    }
    @Override
    @Transactional
    public List<FoodItemResponse> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(this::mapToResponse1)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public Optional<FoodItemResponse> getFoodItemById(Long foodItemId) {
        return foodItemRepository.findByFoodItemId(foodItemId)
                .map(this::mapToResponse1);
    }
    @Override
    @Transactional
    public List<FoodItemResponse> getFoodItemsByEstore(Long estoreId) {

        return foodItemRepository.findByEstore_EstoreId(estoreId).stream()
                .map(this::mapToResponse1)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public FoodItem updateOpenHours(Long foodItemId, AvailableDaysAndHoursDTO openDaysAndHoursDTO) {
        // Fetch the existing Estore from the repository
        FoodItem foodItem = foodItemRepository.findByFoodItemId(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found"));

        /* Get the existing collection reference */
        List<OpenPeriodsForDay> existingPeriods;
        existingPeriods = foodItem.getAvailableDaysAndHours().getOpenPeriodsByDay();
        existingPeriods.clear(); // Clear existing entries without losing the reference

        // Convert DTO to OpenPeriodsForDay entities and add them to the existing list
        openDaysAndHoursDTO.openPeriodsByDay().forEach(periodForDayDTO -> {
            OpenPeriodsForDay openPeriodsForDay = new OpenPeriodsForDay(
                    periodForDayDTO.dayOfWeek(),
                    periodForDayDTO.openPeriods().stream()
                            .map(periodDTO -> new OpenPeriod(
                                    periodDTO.openTime(),
                                    periodDTO.closeTime()
                            ))
                            .collect(Collectors.toList())
            );
            existingPeriods.add(openPeriodsForDay);
        });

        // Save the updated Estore back to the repository
        return foodItemRepository.save(foodItem);
    }

    private FoodItemResponse mapToResponse1(FoodItem foodItem) {

        return new FoodItemResponse(
                foodItem.getFoodItemId(),
                foodItem.getName(),
                foodItem.getImage(),
                foodItem.getSpice_level(),
                foodItem.getCuisine_type(),
                foodItem.getCalories(),
                foodItem.getPrice(), // Directly use the Price object
                foodItem.getDescription(),
                foodItem.getDays_available(),
                foodItem.getVisibility(),
                foodItem.getIngredients(),
                foodItem.getSubCategory(),
                //foodItem.getAvailableDaysAndHours(),
                foodItem.getCombinedIngredients(),
                new EstoreResponse1(foodItem.getEstoreId(),foodItem.getEstore().getName()),  // Assuming EstoreResponse1 has these fields
                new UserResponse1(foodItem.getUserId())
        );
    }

    private FoodItemResponse2 mapToResponse2(FoodItem foodItem) {

        return new FoodItemResponse2(
                foodItem.getFoodItemId(),
                foodItem.getName(),
                foodItem.getImage(),
                foodItem.getCalories(),
                foodItem.getPrice(), // Directly use the Price object
                foodItem.getDescription(),
                new EstoreResponse1(foodItem.getEstoreId(),foodItem.getEstore().getName())  // Assuming EstoreResponse1 has these fields
        );
    }
    private FoodItemResponse mapToResponse(FoodItem foodItem) {
        Estore estore = estoreRepository.findByEstoreId(foodItem.getEstoreId())
                .orElseThrow(() -> new IllegalArgumentException("Estore not found"));
        return new FoodItemResponse(
                foodItem.getFoodItemId(),
                foodItem.getName(),
                foodItem.getImage(),
                foodItem.getSpice_level(),
                foodItem.getCuisine_type(),
                foodItem.getCalories(),
                foodItem.getPrice(), // Directly use the Price object
                foodItem.getDescription(),
                foodItem.getDays_available(),
                foodItem.getVisibility(),
                foodItem.getIngredients(),
                foodItem.getSubCategory(),
                //foodItem.getAvailableDaysAndHours(),
                foodItem.getCombinedIngredients(),
                new EstoreResponse1(foodItem.getEstoreId(), estore.getName()),  // Assuming EstoreResponse1 has these fields
                new UserResponse1(foodItem.getUserId())
        );
    }
    private void updateAvailableDaysAndHours(FoodItem foodItem, AvailableDaysAndHoursDTO availableDaysAndHoursDTO) {
        // Get existing reference
        List<OpenPeriodsForDay> existingPeriods = foodItem.getAvailableDaysAndHours().getOpenPeriodsByDay();
        existingPeriods.clear(); // Clear existing entries without losing the reference

        // Map DTO to entities
        availableDaysAndHoursDTO.openPeriodsByDay().forEach(periodForDayDTO -> {
            OpenPeriodsForDay openPeriodsForDay = new OpenPeriodsForDay(
                    periodForDayDTO.dayOfWeek(),
                    periodForDayDTO.openPeriods().stream()
                            .map(periodDTO -> new OpenPeriod(
                                    periodDTO.openTime(),
                                    periodDTO.closeTime()
                            ))
                            .collect(Collectors.toList())
            );
            existingPeriods.add(openPeriodsForDay);
        });
    }
}
