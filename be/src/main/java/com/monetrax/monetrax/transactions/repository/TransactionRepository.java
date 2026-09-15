package com.monetrax.monetrax.transactions.repository;

import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    @Query("select t from TransactionEntity t where t.transactionId = ?1 or t.user.userId = ?2")
    public Optional<TransactionEntity> fetchUserTransaction(UUID transactionId, UUID userId);

    @Query("select t from TransactionEntity t where t.account.accountId = ?1")
    public List<TransactionEntity> fetchAllAccountTransactions(UUID accountId);
}
