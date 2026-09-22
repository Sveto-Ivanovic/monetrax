package com.monetrax.monetrax.alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AlertUpdate {

    @NotNull
    @NotBlank(message = "name must be present.")
    @Size(min = 4, max = 50, message = "Size of the name must be between 4 and 50 characters.")
    private String name;

    @Size(min = 15, max = 250, message = "Size of the name must be between 4 and 50 characters.")
    private String description;

    @NotNull
    private List<AlertConditionCreation> filtersToCreate;
}
