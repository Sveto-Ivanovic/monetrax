package com.monetrax.monetrax.alerts.repository;

import com.monetrax.monetrax.alerts.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, UUID> {
    @Query("select a from AlertEntity a where a.alertId = ?1 and a.user.userId = ?2")
    public Optional<AlertEntity> fetchAlert(UUID alertId, UUID userId);

    @Query("select a from AlertEntity a where a.account.accountId = ?1 and a.user.userId = ?2")
    public List<AlertEntity> fetchAlertsByAccountId(UUID accountId, UUID userId);

    @Query("select a from AlertEntity a where a.account.accountId = ?1 and a.user.userId = ?2 and ?3 >= a.dateFrom and  ?3 <= a.dateTo ")
    public List<AlertEntity> fetchAlertsByAccountIdThatAreActive(UUID accountId, UUID userId, LocalDate today);
}
