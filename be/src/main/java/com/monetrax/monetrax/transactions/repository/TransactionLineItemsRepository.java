package com.monetrax.monetrax.transactions.repository;

import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionLineItemsRepository extends JpaRepository<TransactionLineItemsEntity, UUID> {
}
