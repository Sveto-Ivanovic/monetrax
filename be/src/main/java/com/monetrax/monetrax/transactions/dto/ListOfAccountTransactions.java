package com.monetrax.monetrax.transactions.dto;

import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListOfAccountTransactions {
    private AccountInformation account;
    private List<TransactionInformationPart> transactions;

    // TODO
    // Put alerts here also
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class TransactionInformationPart {
    private UUID transactionId;
    private String name;
    private String description;
    private BigDecimal amount;
    private String currency;
    private CategoryKind categoryType;
    private OffsetDateTime createdAt;
    private List<String> categories;
}
