package com.monetrax.monetrax.accounts.dto;

import com.monetrax.monetrax.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInformation {

    private UUID accountId;
    private String name;
    private String description;
    private BigDecimal currentBalance;
    private String currency;
    private String institutionName;
    private String accountNumberMasked;
    private boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
