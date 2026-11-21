package com.spirit.Citizen.services.user;

import com.spirit.Citizen.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService {
    UserDetailsService userDetailsService();
    Optional<User> findById(Long id);
}
