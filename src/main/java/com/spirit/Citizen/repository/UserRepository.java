package com.spirit.Citizen.repository;

import com.spirit.Citizen.model.User;
import com.spirit.Citizen.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserRole(UserRole userRole);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
