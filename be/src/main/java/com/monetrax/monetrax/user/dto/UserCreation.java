package com.monetrax.monetrax.user.dto;

import com.monetrax.monetrax.common.validaton.annotation.DateCorrectnessConstraint;
import com.monetrax.monetrax.common.validaton.annotation.EmailCorrectnessConstraint;
import com.monetrax.monetrax.common.validaton.annotation.PasswordFormatConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreation {
    @NotBlank(message = "userEmail must be present.")
    @EmailCorrectnessConstraint
    private String userEmail;

    @NotBlank(message = "userName must be present.")
    @Size(min = 2, max = 40, message = "Size of the user name must be between 2 and 40 characters.")
    private String userName;

    @NotBlank(message = "name must be present.")
    @Size(min = 2, max = 40, message = "Size of the name must be between 2 and 40 characters.")
    private String name;

    @NotBlank(message = "surname must be present.")
    @Size(min = 2, max = 40, message = "Size of the name must be between 2 and 40 characters.")
    private String surname;

    @NotNull(message = "dateOfBirth must be present.")
    @Past(message = "The birth date must be in the past.")
    @DateCorrectnessConstraint(message = "User has to be at least 18 years old.", value = -18, filter = DateCorrectnessConstraint.DateFilter.YEARS)
    private LocalDate dateOfBirth;

    @NotBlank(message = "password must be present.")
    @Size(min = 8, max = 40, message = "Size of the password must be between 6 and 40 characters.")
    @PasswordFormatConstraint
    private String password;
}
