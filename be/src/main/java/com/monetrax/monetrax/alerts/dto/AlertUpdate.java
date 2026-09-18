package com.monetrax.monetrax.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertUpdate {
    private String name;

    private String description;

    private LocalDate dateFrom;

    private LocalDate dateTo;
}
