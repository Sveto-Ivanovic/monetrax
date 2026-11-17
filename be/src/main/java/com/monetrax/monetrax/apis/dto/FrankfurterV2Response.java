package com.monetrax.monetrax.apis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FrankfurterV2Response {
    private LocalDate date;
    private String base;
    private String quote;
    private double rate;
}
