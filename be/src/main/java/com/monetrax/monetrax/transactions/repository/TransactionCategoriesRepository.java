package com.monetrax.monetrax.transactions.repository;

import com.monetrax.monetrax.transactions.entity.TransactionCategoriesEmbeddable;
import com.monetrax.monetrax.transactions.entity.TransactionCategoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCategoriesRepository extends JpaRepository<TransactionCategoriesEntity, TransactionCategoriesEmbeddable> {
}
