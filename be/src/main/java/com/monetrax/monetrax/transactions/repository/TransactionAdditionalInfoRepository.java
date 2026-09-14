package com.monetrax.monetrax.transactions.repository;

import com.monetrax.monetrax.transactions.entity.TransactionAdditionalInfoEntity;
import com.monetrax.monetrax.transactions.entity.TransactionCategoriesEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionAdditionalInfoRepository extends JpaRepository<TransactionAdditionalInfoEntity, UUID> {
    @Query("select t from TransactionAdditionalInfoEntity t where t.transaction.transactionId = ?1")
    public List<TransactionAdditionalInfoEntity> fetchAllTransactionsAdditionalInfo(UUID transactionId);

    @Modifying
    @Query("delete from TransactionAdditionalInfoEntity t where t.transaction.transactionId = ?1")
    int deleteAllTransactionAdditionalInfoByTransactionId(UUID transactionId);
}
