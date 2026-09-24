package com.monetrax.monetrax.transactions.repository;

import com.monetrax.monetrax.transactions.entity.TransactionRecurrenceRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRecurrenceRuleRepository extends JpaRepository<TransactionRecurrenceRuleEntity, UUID> {

    @Query("Select t from TransactionRecurrenceRuleEntity t JOIN FETCH t.sourceTransaction where t.occurrencesGenerated < t.maxOccurrences and t.nextRunDate <= ?1")
    public List<TransactionRecurrenceRuleEntity> fetchAllActiveRules(LocalDate tomorrow);

    @Query("Select t from TransactionRecurrenceRuleEntity t where t.sourceTransaction.transactionId = ?1")
    public Optional<TransactionRecurrenceRuleEntity> fetchRuleWithTransactionId(UUID transactionId);

    @Query("Select t from TransactionRecurrenceRuleEntity t join fetch t.sourceTransaction where t.sourceTransaction.transactionId in ?1")
    public List<TransactionRecurrenceRuleEntity> fetchRulesWithTransactionIds(List<UUID> transactionIds);

    @Query("Select t from TransactionRecurrenceRuleEntity t where t.sourceTransaction.transactionId = ?1 and t.recurrenceRuleId = ?2")
    Optional<TransactionRecurrenceRuleEntity> fetchTransactionRule(UUID transactionId, UUID transactionRuleId);
}
