package com.monetrax.monetrax.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertInformation {

    private UUID alertId;

    private String name;

    private String description;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private List<AlertConditionInformation> filters;

    private List<AlertState> alertStates;

    private boolean breached;

    private boolean active;
}
