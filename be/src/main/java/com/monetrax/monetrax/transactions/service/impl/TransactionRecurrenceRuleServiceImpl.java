package com.monetrax.monetrax.transactions.service.impl;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.accounts.exceptions.NoSuchAccountFound;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
import com.monetrax.monetrax.transactions.dto.TransactionRecurrenceInformation;
import com.monetrax.monetrax.transactions.dto.TransactionRecurrenceResponse;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionRecurrenceRuleEntity;
import com.monetrax.monetrax.transactions.exceptions.MissingTransactionLikeEntityException;
import com.monetrax.monetrax.transactions.mapper.GlobalTransactionMapper;
import com.monetrax.monetrax.transactions.repository.TransactionRecurrenceRuleRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.transactions.service.TransactionRecurrenceRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TransactionRecurrenceRuleServiceImpl implements TransactionRecurrenceRuleService {

    private final TransactionRecurrenceRuleRepository transactionRecurrenceRuleRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final GlobalTransactionMapper globalTransactionMapper;

    public TransactionRecurrenceRuleServiceImpl(TransactionRecurrenceRuleRepository transactionRecurrenceRuleRepository,
                                                TransactionRepository transactionRepository, AccountRepository accountRepository, GlobalTransactionMapper globalTransactionMapper) {
        this.transactionRecurrenceRuleRepository = transactionRecurrenceRuleRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.globalTransactionMapper = globalTransactionMapper;
    }


    @Override
    public TransactionRecurrenceResponse getAllTransactionRulesForAccount(UUID accountId, UUID userId) {
        log.debug("Fetching recurrence rules for account [accountId={}, userId={}]", accountId, userId);

        AccountEntity account = accountRepository.findAccountNonLock(userId, accountId).orElseThrow(() -> {
            log.warn("Recurrence rules fetch failed, account not found [accountId={}, userId={}]", accountId, userId);
            return new NoSuchAccountFound("No such account exists!");
        });
        List<TransactionEntity> transactionEntityList = transactionRepository.fetchAllAccountTransactions(account.getAccountId());
        log.debug("Fetched account transactions for recurrence rules [accountId={}, transactionCount={}]", accountId, transactionEntityList.size());

        List<TransactionRecurrenceRuleEntity> transactionRecurrenceRuleEntityList = transactionRecurrenceRuleRepository
                .fetchRulesWithTransactionIds(transactionEntityList.stream().map(TransactionEntity::getTransactionId).toList());

        List<TransactionRecurrenceInformation> list = new ArrayList<>();
        for(var rule: transactionRecurrenceRuleEntityList){
            list.add(globalTransactionMapper
                    .fromTransactionEntityAndTransactionRecurrenceRuleEntityToTransactionRecurrenceInformation(rule.getSourceTransaction(), rule));
        }

        log.debug("Fetched recurrence rules [accountId={}, userId={}, ruleCount={}]", accountId, userId, list.size());
        return new TransactionRecurrenceResponse(list);
    }

    @Override
    public TransactionCreateUpdateResponse deleteTransactionRule(UUID transactionRuleId, UUID transactionId, UUID userId) {
        log.info("Deleting transaction recurrence rule [ruleId={}, transactionId={}, userId={}]", transactionRuleId, transactionId, userId);

        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(() -> {
            log.warn("Recurrence rule deletion failed, transaction not found [transactionId={}, userId={}]", transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction found !");
        });
        TransactionRecurrenceRuleEntity transactionRecurrenceRuleEntity = transactionRecurrenceRuleRepository.fetchTransactionRule(transactionEntity.getTransactionId(), transactionRuleId).orElseThrow(() -> {
            log.warn("Recurrence rule deletion failed, rule not found [ruleId={}, transactionId={}, userId={}]", transactionRuleId, transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction rule found !");
        });

        transactionRecurrenceRuleRepository.delete(transactionRecurrenceRuleEntity);
        log.info("Transaction recurrence rule deleted [ruleId={}, transactionId={}, userId={}]", transactionRuleId, transactionId, userId);

        return new TransactionCreateUpdateResponse("Successfully deleted the rule", null);
    }
}