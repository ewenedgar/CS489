package edu.miu.horelo.repository;

import edu.miu.horelo.model.FoodSafetyMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodSafetyMessageRepository extends JpaRepository<FoodSafetyMessage, Long> {
}
