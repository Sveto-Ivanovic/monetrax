package com.monetrax.monetrax.accounts.service.impl;

import com.monetrax.monetrax.accounts.dto.AccountCreate;
import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.accounts.dto.AccountUpdate;
import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.accounts.exceptions.NoAccountDataToUpdate;
import com.monetrax.monetrax.accounts.exceptions.NoSuchAccountFound;
import com.monetrax.monetrax.accounts.mapper.AccountMapper;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.accounts.service.AccountService;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountMapper accountMapper, AccountRepository accountRepository, UserRepository userRepository){
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccountInformation getAccountInformation(UUID userId, UUID accountId) {
        log.debug("Fetching account information with params: userId={}, accountId={}", userId, accountId);
        AccountEntity account = accountRepository.findAccountNonLock(userId, accountId).orElseThrow(()->{
            log.warn("Account not found: userId={}, accountId={}", userId, accountId);
            return new NoSuchAccountFound("No such account exists!");
        });
        log.debug("Account information fetched: userId={}, accountId={}", userId, accountId);
        return accountMapper.fromAccountEntityToAccountInformation(account);
    }

    @Override
    public List<AccountInformation> getAccounts(UUID userId, boolean includeArchived) {
        log.debug("Fetching user accounts for params: userId={}, includeArchived={}", userId, includeArchived);
        List<AccountEntity> accountEntities;
        if(includeArchived)
            accountEntities = accountRepository.getAllAccounts(userId);
        else
            accountEntities = accountRepository.getUnArchivedAccounts(userId);
        log.info("Fetched {} account(s): userId={}, includeArchived={}", accountEntities.size(), userId, includeArchived);
        return accountEntities.stream()
                .map(accountMapper::fromAccountEntityToAccountInformation)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountInformation> getArchivedAccounts(UUID userId) {
        log.debug("Fetching archived accounts with params: userId={}", userId);
        List<AccountEntity> accountEntities = accountRepository.getArchivedAccounts(userId);
        log.info("Fetched {} archived account(s): userId={}", accountEntities.size(), userId);
        return accountEntities.stream()
                .map(accountMapper::fromAccountEntityToAccountInformation)
                .collect(Collectors.toList());
    }

    @Override
    public AccountInformation createAccount(AccountCreate accountCreate, UUID userId) {
        log.info("Creating account: userId={}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(()->{
            log.warn("Cannot create account, user not found: userId={}", userId);
            return new NoSuchUserExistsException("No user with id: "+ userId);
        });
        AccountEntity toCreate = accountMapper.fromAccountCreateToAccountEntity(accountCreate, user);
        AccountEntity created = accountRepository.save(toCreate);
        log.info("Account created: userId={}, accountId={}", userId, created.getAccountId());
        return accountMapper.fromAccountEntityToAccountInformation(created);
    }

    @Override
    @Transactional
    public AccountInformation updateAccount(AccountUpdate accountUpdate, UUID accountId, UUID userId) {
        log.info("Updating account: userId={}, accountId={}", userId, accountId);
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(()->{
            log.warn("Cannot update, account not found: userId={}, accountId={}", userId, accountId);
            return new NoSuchAccountFound("No such account exists!");
        });

        if(accountUpdate.getAccountNumberMasked()==null &&
                accountUpdate.getDescription()==null &&
                accountUpdate.getInstitutionName()==null &&
                accountUpdate.getName() == null &&
                accountUpdate.getToggleActivate() == null) {
            log.warn("Update rejected, no fields provided: userId={}, accountId={}", userId, accountId);
            throw new NoAccountDataToUpdate("Please insert field to update.");
        }

        log.debug("Updating fields for accountId={}: accountNumberMasked={}, name={}, description={}, institutionName={}, active={}",
                accountId,
                accountUpdate.getAccountNumberMasked() != null,
                accountUpdate.getName() != null,
                accountUpdate.getDescription() != null,
                accountUpdate.getInstitutionName() != null,
                accountUpdate.getToggleActivate() != null);

        Optional.ofNullable(accountUpdate.getAccountNumberMasked()).ifPresent(account::setAccountNumberMasked);
        Optional.ofNullable(accountUpdate.getName()).ifPresent(account::setName);
        Optional.ofNullable(accountUpdate.getDescription()).ifPresent(account::setDescription);
        Optional.ofNullable(accountUpdate.getInstitutionName()).ifPresent(account::setInstitutionName);
        Optional.ofNullable(accountUpdate.getToggleActivate()).ifPresent(account::setActive);

        AccountEntity accountEntity = accountRepository.save(account);
        log.info("Account updated: userId={}, accountId={}", userId, accountId);
        return accountMapper.fromAccountEntityToAccountInformation(accountEntity);
    }

    @Override
    @Transactional
    public AccountInformation archiveAccount(UUID userId, UUID accountId, boolean archived) {
        log.info("{} account: userId={}, accountId={}", archived ? "Archiving" : "Unarchiving", userId, accountId);
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(()->{
            log.warn("Cannot change archive state, account not found: userId={}, accountId={}", userId, accountId);
            return new NoSuchAccountFound("No such account exists!");
        });

        account.setArchived(archived);
        account.setActive(false);
        AccountEntity accountEntity = accountRepository.save(account);
        log.info("Account archived={} (deactivated): userId={}, accountId={}", archived, userId, accountId);
        return accountMapper.fromAccountEntityToAccountInformation(accountEntity);
    }


}
