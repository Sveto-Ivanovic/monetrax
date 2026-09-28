package com.monetrax.monetrax.auth.repository;

import com.monetrax.monetrax.auth.entity.RefreshTokenEntity;
import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    @Query("select t from RefreshTokenEntity t where t.tokenHash = ?1 and t.revokedAt is null and t.expiresAt > ?2")
    Optional<RefreshTokenEntity> getTokenEntityBasedOnTokenHash(String tokenHash, OffsetDateTime now);


    @Query("select t from RefreshTokenEntity t where t.user.userId = ?1 and t.revokedAt is null and t.expiresAt > ?2")
    Optional<RefreshTokenEntity> getActiveTokenIfExistsBasedOnUser(UUID userId, OffsetDateTime now);
}
