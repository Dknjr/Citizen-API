package com.spirit.Citizen.services.Auth;

import com.spirit.Citizen.dto.AuthResponse;
import com.spirit.Citizen.dto.SigninRequest;
import com.spirit.Citizen.model.User;

import java.util.Optional;

public interface IAuthService {
    String signupUser(SigninRequest signupRequest);
    AuthResponse signin(SigninRequest signinRequest);
    Optional<User> findByEmail(String email);
}
