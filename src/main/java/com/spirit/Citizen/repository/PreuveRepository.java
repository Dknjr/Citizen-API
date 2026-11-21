package com.spirit.Citizen.repository;

import com.spirit.Citizen.model.Preuve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreuveRepository extends JpaRepository<Preuve, Long> {
}