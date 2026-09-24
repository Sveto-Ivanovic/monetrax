package com.monetrax.monetrax.transactions.schedule;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.transactions.entity.TransactionCategoriesEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionRecurrenceRuleEntity;
import com.monetrax.monetrax.transactions.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TransactionScheduler {

    private final TransactionRepository transactionRepository;
    private final TransactionRecurrenceRuleRepository transactionRecurrenceRuleRepository;
    private final TransactionCategoriesRepository transactionCategoriesRepository;
    private final AccountRepository accountRepository;

    public TransactionScheduler(TransactionRepository transactionRepository,
                                TransactionRecurrenceRuleRepository transactionRecurrenceRuleRepository,
                                TransactionCategoriesRepository transactionCategoriesRepository,
                                AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionRecurrenceRuleRepository = transactionRecurrenceRuleRepository;
        this.transactionCategoriesRepository = transactionCategoriesRepository;
        this.accountRepository = accountRepository;
    }

    @Scheduled(cron = "0 59 23 * * *", zone = "UTC")
    //@Scheduled(cron = "0 */2 * * * *", zone = "UTC")
    @Transactional
    public void scheduleTransactionTask() {
        long startNanos = System.nanoTime();
        LocalDate cutoffDate = LocalDate.now(ZoneOffset.UTC).plusDays(1);
        log.info("Recurring transaction job started (cutoff date: {})", cutoffDate);

        try {
            List<TransactionRecurrenceRuleEntity> transactionRecurrenceRuleEntityList = transactionRecurrenceRuleRepository.fetchAllActiveRules(cutoffDate);
            if(transactionRecurrenceRuleEntityList.isEmpty()) {
                log.info("No active recurrence rules due, nothing to do");
                return;
            }
            log.info("Fetched {} active recurrence rule(s) due", transactionRecurrenceRuleEntityList.size());


            // transactionId to TransactionRecurrenceRuleEntity
            Map<UUID, TransactionRecurrenceRuleEntity> transactionRecurrenceRuleEntityMap = transactionRecurrenceRuleEntityList.stream()
                    .collect(Collectors.toMap(x->x.getSourceTransaction().getTransactionId(), x->x));

            List<UUID> transactionIdsToFetch  = transactionRecurrenceRuleEntityList.stream().map(x->x.getSourceTransaction().getTransactionId()).toList();
            log.debug("Source transaction ids to fetch: {}", transactionIdsToFetch);

            // transactionId to TransactionEntity
            Map<UUID, TransactionEntity> transactionEntityMap =
                    transactionRepository.fetchTransactionsForScheduler(transactionIdsToFetch).stream()
                            .collect(Collectors.toMap(TransactionEntity::getTransactionId, x->x));
            log.debug("Fetched {} source transaction(s) out of {} requested", transactionEntityMap.size(), transactionIdsToFetch.size());

            // transaction Id to TransactionCategoryEntity
            Map<UUID, List<TransactionCategoriesEntity>> transactionCategoriesEntityMap = transactionCategoriesRepository.fetchTransactionCategoriesForScheduler(transactionIdsToFetch)
                    .stream()
                    .collect(Collectors.groupingBy(x->x.getId().getTransactionId(), Collectors.mapping(x->x, Collectors.toList())));
            log.debug("Fetched categories for {} source transaction(s)", transactionCategoriesEntityMap.size());



            // rows to update
            List<AccountEntity> accountEntitiesToUpdate = new ArrayList<>();
            List<TransactionRecurrenceRuleEntity> transactionRecurrenceRuleEntityListToUpdate = new ArrayList<>();

            // here we store transactions to save and after save all transactions that are saved
            Map<UUID, TransactionEntity> sourceIdToNewTransaction = new LinkedHashMap<>();

            // looping through all new transactions to save
            for(UUID transactionId: transactionIdsToFetch){

                TransactionEntity existingTransaction = transactionEntityMap.get(transactionId);
                TransactionRecurrenceRuleEntity existingTransactionRecurrenceRule = transactionRecurrenceRuleEntityMap.get(transactionId);

                if (existingTransaction == null) {
                    log.error("Source transaction {} was not returned by fetchTransactionsForScheduler (rule {})",
                            transactionId, existingTransactionRecurrenceRule.getRecurrenceRuleId());
                }

                AccountEntity accountEntity = existingTransaction.getAccount();

                log.debug("Processing rule {} for source transaction {} (unit: {}, interval: {}, nextRunDate: {}, occurrences: {}/{})",
                        existingTransactionRecurrenceRule.getRecurrenceRuleId(),
                        transactionId,
                        existingTransactionRecurrenceRule.getRecurrenceUnit(),
                        existingTransactionRecurrenceRule.getIntervalCount(),
                        existingTransactionRecurrenceRule.getNextRunDate(),
                        existingTransactionRecurrenceRule.getOccurrencesGenerated(),
                        existingTransactionRecurrenceRule.getMaxOccurrences());

                List<TransactionCategoriesEntity> categoriesForThis = transactionCategoriesEntityMap.get(transactionId);
                if (categoriesForThis == null || categoriesForThis.isEmpty()) {
                    log.warn("Source transaction {} has no categories despite the 'at least one category' invariant — generating anyway, investigate", transactionId);
                }

                BigDecimal previousBalance = accountEntity.getCurrentBalance();

                BigDecimal updatedCurrentBalance = switch (existingTransaction.getCategoryType()) {
                    case INCOME, ADJUSTMENT_PLUS, TRANSFER_FROM -> accountEntity.getCurrentBalance().add(existingTransaction.getAmountNative());
                    case EXPENSE, ADJUSTMENT_MINUS, TRANSFER_TO -> accountEntity.getCurrentBalance().subtract(existingTransaction.getAmountNative());
                };

                accountEntity.setCurrentBalance(updatedCurrentBalance);
                accountEntity.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                log.debug("Account {} balance {} -> {} ({} {})",
                        accountEntity.getAccountId(), previousBalance, updatedCurrentBalance,
                        existingTransaction.getCategoryType(), existingTransaction.getAmountNative());

                //
                accountEntitiesToUpdate.add(accountEntity);

                // new transaction to save
                TransactionEntity newTransactionEntity = TransactionEntity.builder()
                        .account(accountEntity)
                        .categoryType(existingTransaction.getCategoryType())
                        .amount(existingTransaction.getAmount())
                        .amountNative(existingTransaction.getAmountNative())
                        .conversionFactor(existingTransaction.getConversionFactor())
                        .createdAt(OffsetDateTime.of(existingTransactionRecurrenceRule.getNextRunDate(), LocalTime.of(12,0), ZoneOffset.UTC))
                        .currency(existingTransaction.getCurrency())
                        .description(existingTransaction.getDescription())
                        .name(existingTransaction.getName())
                        .user(existingTransaction.getUser())
                        .build();

                //
                sourceIdToNewTransaction.put(transactionId, newTransactionEntity);

                existingTransactionRecurrenceRule.setLastRunDate( existingTransactionRecurrenceRule.getNextRunDate());
                existingTransactionRecurrenceRule.setOccurrencesGenerated(existingTransactionRecurrenceRule.getOccurrencesGenerated()+1);

                LocalDate nextRunDate = switch (existingTransactionRecurrenceRule.getRecurrenceUnit()){
                    case DAY -> existingTransactionRecurrenceRule.getNextRunDate().plusDays(existingTransactionRecurrenceRule.getIntervalCount());
                    case WEEK -> existingTransactionRecurrenceRule.getNextRunDate().plusWeeks(existingTransactionRecurrenceRule.getIntervalCount());
                    case MONTH ->  existingTransactionRecurrenceRule.getNextRunDate().plusMonths(existingTransactionRecurrenceRule.getIntervalCount());
                    case YEAR -> existingTransactionRecurrenceRule.getNextRunDate().plusYears(existingTransactionRecurrenceRule.getIntervalCount());
                };
                existingTransactionRecurrenceRule.setNextRunDate(nextRunDate);
                log.debug("Rule {} advanced: lastRunDate={}, nextRunDate={}, occurrencesGenerated={}",
                        existingTransactionRecurrenceRule.getRecurrenceRuleId(),
                        existingTransactionRecurrenceRule.getLastRunDate(),
                        nextRunDate,
                        existingTransactionRecurrenceRule.getOccurrencesGenerated());

                //
                transactionRecurrenceRuleEntityListToUpdate.add(existingTransactionRecurrenceRule);
            }

            log.info("Saving {} new transaction(s), {} account(s), {} rule(s)",
                    sourceIdToNewTransaction.size(), accountEntitiesToUpdate.size(), transactionRecurrenceRuleEntityListToUpdate.size());

            transactionRepository.saveAll(sourceIdToNewTransaction.values());
            accountRepository.saveAll(accountEntitiesToUpdate);
            transactionRecurrenceRuleRepository.saveAll(transactionRecurrenceRuleEntityListToUpdate);

            // save transaction categories
            List<TransactionCategoriesEntity> transactionCategoriesToSave = new ArrayList<>();

            for(var savedTransaction: sourceIdToNewTransaction.entrySet()){
                List<TransactionCategoriesEntity> categoriesEntityList = transactionCategoriesEntityMap.get(savedTransaction.getKey());

                if (categoriesEntityList == null) {
                    log.warn("No categories to copy for source transaction {} (new transaction {})",
                            savedTransaction.getKey(), savedTransaction.getValue().getTransactionId());
                }

                for(var category: categoriesEntityList)
                    transactionCategoriesToSave.add(TransactionCategoriesEntity.builder()
                            .category(category.getCategory())
                            .transaction(savedTransaction.getValue())
                            .build());

            }

            log.debug("Saving {} transaction categor(ies)", transactionCategoriesToSave.size());
            transactionCategoriesRepository.saveAll(transactionCategoriesToSave);

            log.info("Recurring transaction job finished: generated {} transaction(s) in {} ms",
                    sourceIdToNewTransaction.size(), (System.nanoTime() - startNanos) / 1_000_000);
        } catch (RuntimeException e) {
            log.error("Recurring transaction job failed after {} ms, transaction will roll back",
                    (System.nanoTime() - startNanos) / 1_000_000, e);
            throw e;
        }
    }

}