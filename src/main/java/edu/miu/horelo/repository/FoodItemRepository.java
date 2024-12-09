package edu.miu.horelo.repository;

import edu.miu.horelo.model.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByEstore_EstoreId(Long foodItemId);
    List<FoodItem> findFoodItemsByEstore_EstoreId(Long estoreId);
    //@Query(value = "SELECT * FROM food_items WHERE food_item_id = :foodItemId", nativeQuery = true)
    //Optional<FoodItem> findByFoodItemId(@Param("foodItemId") Long foodItemId);
    Optional<FoodItem> findByFoodItemId( Long foodItemId);

    @Query("""
        SELECT f
        FROM food_items f
        JOIN estores e ON f.estoreId = e.estoreId
        WHERE (:foodItemName IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :foodItemName, '%')))
          AND (:estoreName IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :estoreName, '%')))
          AND (:city IS NULL OR LOWER(e.primaryAddress.city) LIKE LOWER(CONCAT('%', :city, '%')))
          AND (:street IS NULL OR LOWER(e.primaryAddress.street) LIKE LOWER(CONCAT('%', :street, '%')))
          AND (:state IS NULL OR LOWER(e.primaryAddress.state) LIKE LOWER(CONCAT('%', :state, '%')))
          AND (:productName IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :productName, '%')))
    """)
    Page<FoodItem> searchByCriteria(
            @Param("foodItemName") String foodItemName,
            @Param("estoreName") String estoreName,
            @Param("city") String city,
            @Param("street") String street,
            @Param("state") String state,
            @Param("productName") String productName,
            Pageable pageable
    );

    Page<FoodItem> findFoodItemsByEstore_EstoreId(Long estoreId, Pageable pageable);

}
