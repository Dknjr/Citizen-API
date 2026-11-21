package com.spirit.Citizen.dto;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String email;
    private String confirmationCode;
    private String newPassword;
}
