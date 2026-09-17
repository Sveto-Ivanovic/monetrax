package com.monetrax.monetrax.alerts.repository;

import com.monetrax.monetrax.alerts.entity.AlertConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlertConditionRepository extends JpaRepository<AlertConditionEntity, UUID> {
}
