package com.monetrax.monetrax.accounts.service;

import com.monetrax.monetrax.accounts.dto.AccountCreate;
import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.accounts.dto.AccountUpdate;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountInformation getAccountInformation(UUID userId, UUID accountId);
    List<AccountInformation> getAccounts(UUID userId, boolean includeArchived);
    List<AccountInformation> getArchivedAccounts(UUID userId);
    AccountInformation createAccount(AccountCreate accountCreate, UUID userId);
    AccountInformation updateAccount(AccountUpdate accountUpdate,UUID accountId, UUID userId);
    AccountInformation archiveAccount(UUID userId, UUID accountId, boolean archived);
}
