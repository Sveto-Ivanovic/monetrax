package com.monetrax.monetrax.ai.repository;

import com.monetrax.monetrax.ai.entity.ApiKeysEntity;
import com.monetrax.monetrax.ai.entity.KeyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeysRepository extends JpaRepository<ApiKeysEntity, UUID> {

    @Query("select a from ApiKeysEntity a where a.keyType = ?1 and  a.user.userId = ?2")
    public Optional<ApiKeysEntity> fetchApiKey(KeyType keyType, UUID userId);

}
