package com.spirit.Citizen.services.Auth;

import com.spirit.Citizen.config.GlobalExceptionHandler;
import com.spirit.Citizen.config.InvalidCredentialsException;
import com.spirit.Citizen.config.UserNotFoundException;
import com.spirit.Citizen.dto.AuthResponse;
import com.spirit.Citizen.dto.RefreshTokenRequest;
import com.spirit.Citizen.dto.SignUpRequest;
import com.spirit.Citizen.dto.SigninRequest;
import com.spirit.Citizen.model.Profil;
import com.spirit.Citizen.model.User;
import com.spirit.Citizen.model.UserRole;
import com.spirit.Citizen.repository.ProfilRepository;
import com.spirit.Citizen.repository.UserRepository;
import com.spirit.Citizen.services.user.EmailService;
import com.spirit.Citizen.services.user.NotificationServices;
import com.spirit.Citizen.services.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {


    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationServices notificationService;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ProfilRepository profilRepository;


    @Transactional
    public User signup(SignUpRequest signUpRequest) {
        // Vérification de l'email
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new GlobalExceptionHandler.UserAlreadyExistsException("Email already exists");
        }

        // Vérification de la longueur du mot de passe
        if (signUpRequest.getPassword().length() < 6) {
            throw new GlobalExceptionHandler.InvalidPasswordException("Password is too short");
        }

        // Vérification du pseudo unique
        if (profilRepository.existsByPseudo(signUpRequest.getUsername())) {
            throw new GlobalExceptionHandler.InvalidUsernameException("Pseudo already exists");
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setUserRole(UserRole.USER);

        userRepository.save(user);

        // Création du profil associé
        Profil profil = new Profil();
        profil.setPseudo(signUpRequest.getUsername());
        profil.setUser(user);

        // Sauvegarde du profil
        profilRepository.save(profil);

        // Mise à jour de l'utilisateur avec le profil
        user.setProfil(profil);
        userRepository.save(user);

        // Envoi de l'email de bienvenue
        try {
            emailService.sendWelcomeEmail(user.getEmail(), profil.getPseudo());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Erreur lors de l'envoi de l'email de bienvenue.", e);
        }

        // Envoi de la notification de bienvenue
        notificationService.envoyerNotificationDeBienvenue(user);

        return user;
    }

//    public void initiatePasswordChange(String email) throws IOException {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            String confirmationCode = confirmationCodeGen.generateConfirmationCode();
//            confirmationCodeRepo.storeConfirmationCode(user.getUserId(), confirmationCode);
//            emailService.sendPasswordChangeEmail(user.getEmail(), confirmationCode);
//        } else {
//            throw new IllegalArgumentException("User with email " + email + " not found.");
//        }
//    }
//
//    public boolean confirmPasswordChange(PasswordChangeRequest passwordChangeRequest) {
//        Optional<User> userOptional = userRepository.findByEmail(passwordChangeRequest.getEmail());
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (confirmUser(user.getEmail(), passwordChangeRequest.getConfirmationCode())) {
//                user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
//                userRepository.save(user);
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public void resendConfirmationCode(String email) {
//        userRepository.findByEmail(email)
//                .ifPresentOrElse(user -> {
//                    String confirmationCode = confirmationCodeGen.generateConfirmationCode();
//                    confirmationCodeRepo.storeConfirmationCode(user.getUserId(), confirmationCode);
//                    try {
//                        emailService.sendConfirmationEmail(user.getEmail(), confirmationCode);
//                    } catch (IOException e) {
//                        throw new RuntimeException("Error sending confirmation email: " + e.getMessage());
//                    }
//                }, () -> {
//                    throw new IllegalArgumentException("Email not found.");
//                });
//    }


    @Override
    public String signupUser(SigninRequest signupRequest) {
        return null;
    }

    private void validateSignupRequest(SignUpRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new GlobalExceptionHandler.UserAlreadyExistsException("Email already exists");
        }
        if (signupRequest.getPassword().length() < 6) {
            throw new GlobalExceptionHandler.InvalidPasswordException("Password is too short");
        }
    }

    @Override
    public AuthResponse signin(SigninRequest signinRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signinRequest.getEmail(),
                            signinRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: "
                        + signinRequest.getEmail()));

        Long userId = user.getId();
        String jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        long expiresAt = System.currentTimeMillis() + 1000 * 60 * 60 * 10; // 10 hours
        String role = user.getAuthorities().iterator().next().getAuthority();


        return buildAuthResponse(user, userId, jwt, refreshToken, expiresAt, role);
    }

    private AuthResponse buildAuthResponse(User user, Long userId, String jwt, String refreshToken, long expiresAt, String role) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUserId(userId);
        authResponse.setToken(jwt);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setExpiresAt(expiresAt);
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(role);
        authResponse.setMessage("Authentication successful");
        return authResponse;
    }

    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getRefreshtoken());
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (jwtService.isTokenValid(refreshTokenRequest.getRefreshtoken(), user)) {
            String jwt = jwtService.generateToken(user);
            long expiresAt = System.currentTimeMillis() + 1000 * 60 * 60 * 10; // 10 hours
            String role = user.getUserRole().toString();


            Long userId = user.getId();

            return buildAuthResponse(user, userId, jwt, refreshTokenRequest.getRefreshtoken(), expiresAt, role);
        }

        throw new IllegalArgumentException("Invalid refresh token");
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
