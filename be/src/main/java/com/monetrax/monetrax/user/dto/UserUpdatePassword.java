package com.monetrax.monetrax.user.dto;

import com.monetrax.monetrax.common.validaton.annotation.PasswordFormatConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdatePassword {
    @NotBlank(message = "old password must be present.")
    @Size(min = 8, max = 40, message = "Size of the old password must be between 8 and 40 characters.")
    @PasswordFormatConstraint(message = "Invalid Password format, old password must contain at least 1 uppercase, lowercase, number and symbol.")
    private String oldPassword;


    @NotBlank(message = "new password must be present.")
    @Size(min = 8, max = 40, message = "Size of the new password must be between 8 and 40 characters.")
    @PasswordFormatConstraint(message = "Invalid Password format, new password must contain at least 1 uppercase, lowercase, number and symbol.")
    private String newPassword;
}
