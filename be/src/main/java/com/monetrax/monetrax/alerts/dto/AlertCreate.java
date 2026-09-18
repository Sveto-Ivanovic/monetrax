package com.monetrax.monetrax.alerts.dto;

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

    private String name;

    private String description;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private List<AlertConditionCreation> filtersToCreate;
}
