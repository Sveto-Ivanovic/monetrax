package com.monetrax.monetrax.alerts.repository;

import com.monetrax.monetrax.alerts.entity.AlertConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertConditionRepository extends JpaRepository<AlertConditionEntity, UUID> {

    @Query("Select a from AlertConditionEntity a where a.alert.alertId = ?1")
    public List<AlertConditionEntity> fetchAlertsConditions(UUID alertId);

    @Query("Select a from AlertConditionEntity a JOIN FETCH a.alert JOIN FETCH a.category where a.alert.alertId in ?1")
    public List<AlertConditionEntity> fetchAlertConditionsForAccount(List<UUID> alertIds);
}
