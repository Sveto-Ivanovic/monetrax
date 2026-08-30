package com.monetrax.monetrax.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInformation {
    private String userEmail;
    private String userName;
    private String name;
    private String surname;
    private String role;
    private LocalDate dateOfBirth;
    private boolean hasFinishedOnboarding;
    private boolean hasVerifiedEmail;
    private String additionalInfo;
}
