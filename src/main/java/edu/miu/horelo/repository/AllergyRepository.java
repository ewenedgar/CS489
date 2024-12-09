package edu.miu.horelo.repository;

import edu.miu.horelo.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AllergyRepository extends JpaRepository<Allergy, Integer> {
    void deleteAllByAllergyId(Integer id);

    @Query("SELECT a FROM Allergy a WHERE a.allergyId = :id")
    Optional<Allergy> findByAllergyId(@Param("id") Integer id);


    List<Allergy> findAllByUser_UserId(Integer userId);
}
