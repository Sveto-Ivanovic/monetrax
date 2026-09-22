package com.monetrax.monetrax.alerts.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertCreate {

    @NotNull
    @NotBlank(message = "name must be present.")
    @Size(min = 4, max = 50, message = "Size of the name must be between 4 and 50 characters.")
    private String name;

    @Size(min = 15, max = 250, message = "Size of the name must be between 4 and 50 characters.")
    private String description;

    @NotNull
    private LocalDate dateFrom;

    @NotNull
    @Future(message = "The date to, must be in the future.")
    private LocalDate dateTo;

    @NotNull
    private List<AlertConditionCreation> filtersToCreate;

    private AlertRecurrenceRule alertRecurrenceRule;
}
