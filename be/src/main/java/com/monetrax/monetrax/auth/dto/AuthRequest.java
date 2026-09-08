package com.monetrax.monetrax.auth.dto;

import com.monetrax.monetrax.common.validaton.annotation.EmailCorrectnessConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequest {
    @NotNull
    @NotBlank
    @EmailCorrectnessConstraint(message = "Email must not be empty.")
    public String email;
    @NotNull
    @NotBlank
    public String password;
}
