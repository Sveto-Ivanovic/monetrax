package com.monetrax.monetrax.transactions.repository;

import com.monetrax.monetrax.transactions.entity.TransactionAdditionalInfoEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionLineItemsRepository extends JpaRepository<TransactionLineItemsEntity, UUID> {
    @Query("select t from TransactionLineItemsEntity t where t.transaction.transactionId = ?1")
    public List<TransactionLineItemsEntity> fetchAllTransactionsLineProducts(UUID transactionId);
}
