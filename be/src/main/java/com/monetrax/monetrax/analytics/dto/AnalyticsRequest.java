package com.monetrax.monetrax.analytics.dto;

import com.monetrax.monetrax.categories.entity.CategoryKind;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsRequest {

    @NotNull
    private LocalDate dateFrom;
    @NotNull
    private LocalDate dateTo;
    private List<UUID> accountIds;
    @NotNull
    private GroupByTypes groupBy;

}
