package com.monetrax.monetrax.alerts.mapper;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.alerts.dto.*;
import com.monetrax.monetrax.alerts.entity.AlertConditionEntity;
import com.monetrax.monetrax.alerts.entity.AlertEntity;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.user.entity.UserEntity;
import jdk.jfr.Category;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class AlertMapper {
    public AlertInformation fromAlertEntityToAlertInformation(AlertEntity alertEntity, List<AlertConditionInformation> filters,
                                                              List<AlertState> alertStates, boolean  isBreached, boolean isActive){
        return AlertInformation.builder()
                .alertId(alertEntity.getAlertId())
                .active(isActive)
                .alertStates(alertStates)
                .dateFrom(alertEntity.getDateFrom())
                .dateTo(alertEntity.getDateTo())
                .description(alertEntity.getDescription())
                .filters(filters)
                .breached(isBreached)
                .name(alertEntity.getName())
                .build();
    }

    public AlertEntity fromAlertCreateToAlertEntity(AlertCreate alertCreate, AccountEntity account,  UserEntity user){

        return AlertEntity.builder()
                .account(account)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .dateFrom(alertCreate.getDateFrom())
                .dateTo(alertCreate.getDateTo())
                .description(alertCreate.getDescription())
                .name(alertCreate.getName())
                .user(user)
                .build();
    }


    public AlertConditionInformation fromAlertConditionEntityToAlertConditionInformation(AlertConditionEntity alertConditionEntity){

        return AlertConditionInformation.builder()
                .categoryId(alertConditionEntity.getCategory().getCategoryId())
                .categoryName(alertConditionEntity.getCategoryName())
                .conditionId(alertConditionEntity.getConditionId())
                .limitValueHigh(alertConditionEntity.getLimitValueHigh())
                .limitValueLowOrEqual(alertConditionEntity.getLimitValueLowOrEqual())
                .ruleType(alertConditionEntity.getRuleType())
                .build();
    }

    public AlertConditionEntity fromAlertConditionCreateToAlertConditionEntity(AlertConditionCreation alertConditionCreation, AlertEntity alert, CategoryEntity category){

        return AlertConditionEntity.builder()
                .alert(alert)
                .category(category)
                .categoryName(category.getName())
                .limitValueHigh(alertConditionCreation.getLimitValueHigh())
                .limitValueLowOrEqual(alertConditionCreation.getLimitValueLowOrEqual())
                .ruleType(alertConditionCreation.getRuleType())
                .build();
    }

}
