package com.monetrax.monetrax.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertCreateUpdateDeleteResponse {
    private String msg;
    private UUID id;
}
