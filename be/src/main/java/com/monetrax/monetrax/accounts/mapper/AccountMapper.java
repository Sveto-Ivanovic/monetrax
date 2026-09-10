package com.monetrax.monetrax.accounts.mapper;

import com.monetrax.monetrax.accounts.dto.AccountCreate;
import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class AccountMapper {
    public AccountInformation fromAccountEntityToAccountInformation(AccountEntity accountEntity){

        return AccountInformation.builder()
                .accountId(accountEntity.getAccountId())
                .accountNumberMasked(accountEntity.getAccountNumberMasked())
                .active(accountEntity.isActive())
                .currency(accountEntity.getCurrency())
                .currentBalance(accountEntity.getCurrentBalance())
                .description(accountEntity.getDescription())
                .institutionName(accountEntity.getInstitutionName())
                .name(accountEntity.getName())
                .build();
    }

    public AccountEntity fromAccountCreateToAccountEntity(AccountCreate accountCreate, UserEntity user){
        return AccountEntity.builder()
                .accountNumberMasked(accountCreate.getAccountNumberMasked())
                .active(true)
                .archived(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .currency(accountCreate.getCurrency())
                .currentBalance(accountCreate.getInitialBalance())
                .description(accountCreate.getDescription())
                .institutionName(accountCreate.getInstitutionName())
                .name(accountCreate.getName())
                .user(user)
                .build();
    }
}
