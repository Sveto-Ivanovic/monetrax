package com.monetrax.monetrax.transactions.repository;

import com.monetrax.monetrax.transactions.entity.TransactionCategoriesEmbeddable;
import com.monetrax.monetrax.transactions.entity.TransactionCategoriesEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionCategoriesRepository extends JpaRepository<TransactionCategoriesEntity, TransactionCategoriesEmbeddable> {
    @Query("select t from TransactionCategoriesEntity t where t.id.transactionId = ?1")
    public List<TransactionCategoriesEntity> fetchAllTransactionCategoryIds(UUID transactionId);

    @Modifying
    @Query("delete from TransactionCategoriesEntity t where t.id.transactionId = ?1")
    int deleteAllTransactionCategoriesByTransactionId(UUID transactionId);

    @Query("select t from TransactionCategoriesEntity t where t.id.transactionId in ?1")
    List<TransactionCategoriesEntity> fetchAllTransactionCategoryIdsIn(List<UUID> transactionIds);

}
