package com.monetrax.monetrax.transactions.repository;

import com.monetrax.monetrax.analytics.dto.GroupByTypes;
import com.monetrax.monetrax.analytics.dto.MonthlyBalance;
import com.monetrax.monetrax.analytics.dto.MonthlyExpenses;
import com.monetrax.monetrax.analytics.dto.MonthlyIncome;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("select t from TransactionEntity t where t.transactionId = ?1 or t.user.userId = ?2")
    public Optional<TransactionEntity> fetchUserTransaction(UUID transactionId, UUID userId);

    @Query("select t from TransactionEntity t where t.account.accountId = ?1")
    public List<TransactionEntity> fetchAllAccountTransactions(UUID accountId);

    @Query("select t from TransactionEntity t where t.user.userId = ?1")
    public List<TransactionEntity> fetchAllUserTransactions(UUID userId);

    @Query("select t from TransactionEntity t where t.user.userId = ?1 and t.account.accountId = ?2 and  t.createdAt >= ?3 and  t.createdAt < ?4")
    public List<TransactionEntity> fetchUserTransactionsInsideSpecifiedDate(UUID userId, UUID accountId, OffsetDateTime dateFrom, OffsetDateTime dateTo);

    @Query("select t from TransactionEntity t join fetch t.user join fetch t.account where t.transactionId in ?1")
    public List<TransactionEntity> fetchTransactionsForScheduler(List<UUID> transactionIds);

    @Query("""
    select sum(t.amount) as amount,
           'EUR' as currency,
           format(t.createdAt as 'yyyy-MM') as month
    from TransactionEntity t
    where t.account.accountId = ?1
      and t.createdAt >= ?2
      and t.categoryType in ?3
    group by format(t.createdAt as 'yyyy-MM')
    order by format(t.createdAt as 'yyyy-MM') asc
    """)
    List<MonthlyBalance> findMonthlyAmountsByMonth(UUID id, OffsetDateTime from, List<CategoryKind> categoryKindList);

    @Query("select t from TransactionEntity t where t.user.userId = ?1 order by t.createdAt desc")
    List<TransactionEntity> findRecentTransactions(UUID userId, Pageable pageable);

    @Query("select t from TransactionEntity t where t.user.userId = ?1 and t.createdAt >= ?2 and t.createdAt <= ?3")
    List<TransactionEntity> findThisMonthsTransactions(UUID userId, OffsetDateTime from, OffsetDateTime to);

}
