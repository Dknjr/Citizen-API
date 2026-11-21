package com.spirit.Citizen.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private Long userId;
    private String refreshToken;
    private long expiresAt;
    private String email;
    private String role;
    private String message;

}
