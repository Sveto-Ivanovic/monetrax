package com.monetrax.monetrax.accounts.dto;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountListResponse {
    private List<AccountInformation> entityList;
    private String message;
}
