package edu.miu.horelo.repository;

import edu.miu.horelo.model.SpecialEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpecialEventRepository extends JpaRepository<SpecialEvent, Long> {

    Optional<SpecialEvent> findBySpecialEventId(Long specialEventId);

    boolean existsBySpecialEventId(Long specialEventId);

    void deleteBySpecialEventId(Long specialEventId);

    Page<SpecialEvent> findSpecialEventByEstore_EstoreId(Long estoreId, Pageable pageable);

    List<SpecialEvent> findAllByEventDate(LocalDate dateTime);
}
