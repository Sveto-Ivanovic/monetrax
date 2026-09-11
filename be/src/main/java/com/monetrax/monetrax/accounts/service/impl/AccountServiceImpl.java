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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(()->{
           return new NoSuchAccountFound("No such account exists!");
        });
        return accountMapper.fromAccountEntityToAccountInformation(account);
    }

    @Override
    public List<AccountInformation> getAccounts(UUID userId, boolean includeArchived) {
        List<AccountEntity> accountEntities;
        if(includeArchived)
            accountEntities = accountRepository.getAllAccounts(userId);
        else
            accountEntities = accountRepository.getUnArchivedAccounts(userId);
        return accountEntities.stream()
                .map(accountMapper::fromAccountEntityToAccountInformation)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountInformation> getArchivedAccounts(UUID userId) {
        List<AccountEntity> accountEntities = accountRepository.getArchivedAccounts(userId);
        return accountEntities.stream()
                .map(accountMapper::fromAccountEntityToAccountInformation)
                .collect(Collectors.toList());
    }

    @Override
    public AccountInformation createAccount(AccountCreate accountCreate, UUID userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));
        AccountEntity toCreate = accountMapper.fromAccountCreateToAccountEntity(accountCreate, user);
        AccountEntity created = accountRepository.save(toCreate);
        return accountMapper.fromAccountEntityToAccountInformation(created);
    }

    @Override
    public AccountInformation updateAccount(AccountUpdate accountUpdate, UUID accountId, UUID userId) {
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(()->{
            return new NoSuchAccountFound("No such account exists!");
        });

        if(accountUpdate.getAccountNumberMasked()==null &&
                accountUpdate.getDescription()==null &&
                accountUpdate.getInstitutionName()==null &&
                accountUpdate.getName() == null &&
                accountUpdate.getToggleActivate() == null)
            throw new NoAccountDataToUpdate("Please insert field to update.");

        Optional.ofNullable(accountUpdate.getAccountNumberMasked()).ifPresent(account::setAccountNumberMasked);
        Optional.ofNullable(accountUpdate.getName()).ifPresent(account::setName);
        Optional.ofNullable(accountUpdate.getDescription()).ifPresent(account::setDescription);
        Optional.ofNullable(accountUpdate.getInstitutionName()).ifPresent(account::setInstitutionName);
        Optional.ofNullable(accountUpdate.getToggleActivate()).ifPresent(account::setActive);

        AccountEntity accountEntity = accountRepository.save(account);
        return accountMapper.fromAccountEntityToAccountInformation(accountEntity);
    }

    @Override
    public AccountInformation archiveAccount(UUID userId, UUID accountId, boolean archived) {
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(()->{
            return new NoSuchAccountFound("No such account exists!");
        });

        account.setArchived(archived);
        account.setActive(false);
        AccountEntity accountEntity = accountRepository.save(account);
        return accountMapper.fromAccountEntityToAccountInformation(accountEntity);
    }


}
