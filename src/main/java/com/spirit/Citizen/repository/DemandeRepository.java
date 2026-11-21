package com.spirit.Citizen.repository;

import com.spirit.Citizen.model.Demande;
import com.spirit.Citizen.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    List<Demande> findByUser(User user);
    List<Demande> findByCreateAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
