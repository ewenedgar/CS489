package edu.miu.horelo.repository;

import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstoreRepository extends JpaRepository<Estore, Long> {
    List<Estore> findByEditor(User user);
    List<Estore> findByCreator(User user);
    @Query(value = "SELECT * FROM estores WHERE estore_id = :estoreId", nativeQuery = true)
    Optional<Estore> findByEstoreId(@Param("estoreId") Long estoreId);

    @Query("""
        SELECT e 
        FROM estores e 
        WHERE (:areaCode IS NULL OR e.primaryAddress.zipCode LIKE CONCAT('%', :areaCode, '%'))
          AND (:street IS NULL OR LOWER(e.primaryAddress.street) LIKE LOWER(CONCAT('%', :street, '%')))
          AND (:city IS NULL OR LOWER(e.primaryAddress.city) LIKE LOWER(CONCAT('%', :city, '%')))
          AND (:state IS NULL OR LOWER(e.primaryAddress.state) LIKE LOWER(CONCAT('%', :state, '%')))
    """)
    Page<Estore> searchByAddress(
            @Param("areaCode") String areaCode,
            @Param("street") String street,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable
    );
}
