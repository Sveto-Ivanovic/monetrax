package com.monetrax.monetrax.ai.repository;

import com.monetrax.monetrax.ai.entity.MessageHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageHistoryRepository extends JpaRepository<MessageHistoryEntity, UUID> {
}
