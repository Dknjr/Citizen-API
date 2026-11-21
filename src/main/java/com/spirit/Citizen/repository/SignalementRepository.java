package com.spirit.Citizen.repository;

import com.spirit.Citizen.model.Categories;
import com.spirit.Citizen.model.Signalement;
import com.spirit.Citizen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SignalementRepository extends JpaRepository<Signalement, Long> {
    List<Signalement> findByUser(User user);
    List<Signalement> findByCreateAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Signalement> findByCategoriesAndOptionSignalementAndCreateAtBetween(Categories categories, String optionSignalement, LocalDateTime startDate, LocalDateTime endDate);
}
