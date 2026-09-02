package com.monetrax.monetrax.user.dto;

import com.monetrax.monetrax.common.validaton.annotation.DateCorrectnessConstraint;
import com.monetrax.monetrax.common.validaton.annotation.EmailCorrectnessConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdate {
    @EmailCorrectnessConstraint
    private String userEmail;

    @Size(min = 2, max = 40, message = "Size of the user name must be between 2 and 40 characters.")
    private String userName;

    @Size(min = 2, max = 40, message = "Size of the name must be between 2 and 40 characters.")
    private String name;

    @Size(min = 2, max = 40, message = "Size of the name must be between 2 and 40 characters.")
    private String surname;

    @Past(message = "The birth date must be in the past.")
    @DateCorrectnessConstraint(message = "User has to be at least 18 years old.", value = -18, filter = DateCorrectnessConstraint.DateFilter.YEARS)
    private LocalDate dateOfBirth;
}
