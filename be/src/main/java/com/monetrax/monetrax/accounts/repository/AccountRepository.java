package com.monetrax.monetrax.accounts.repository;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    @Query("Select a from AccountEntity a where a.user.userId = ?1 and a.archived = true")
    List<AccountEntity> getArchivedAccounts(UUID userId);

    @Query("Select a from AccountEntity a where a.user.userId = ?1 and a.archived = false")
    List<AccountEntity> getUnArchivedAccounts(UUID userId);

    @Query("Select a from AccountEntity a where a.user.userId = ?1")
    List<AccountEntity> getAllAccounts(UUID userId);

    @Query("Select a from AccountEntity a where a.user.userId = ?1 and a.accountId = ?2")
    Optional<AccountEntity> getAccount(UUID userId, UUID accountId);

    @Query("Select a.archived from AccountEntity a where a.user.userId = ?1 and a.accountId = ?2")
    Optional<Boolean> isAccountArchived(UUID userId, UUID accountId);
}
