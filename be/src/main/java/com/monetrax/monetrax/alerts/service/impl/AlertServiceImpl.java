package com.monetrax.monetrax.alerts.service.impl;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.alerts.dto.*;
import com.monetrax.monetrax.alerts.entity.AlertConditionEntity;
import com.monetrax.monetrax.alerts.entity.AlertEntity;
import com.monetrax.monetrax.alerts.entity.RuleType;
import com.monetrax.monetrax.alerts.exceptions.InvalidInputException;
import com.monetrax.monetrax.alerts.exceptions.NoSuchAlertException;
import com.monetrax.monetrax.alerts.mapper.AlertMapper;
import com.monetrax.monetrax.alerts.repository.AlertConditionRepository;
import com.monetrax.monetrax.alerts.repository.AlertRepository;
import com.monetrax.monetrax.alerts.service.AlertService;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.exceptions.NoSuchCategoryExistsException;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.transactions.entity.TransactionCategoriesEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.repository.TransactionCategoriesRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final AlertConditionRepository alertConditionRepository;
    private final AlertMapper alertMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionCategoriesRepository transactionCategoriesRepository;

    public AlertServiceImpl(AlertRepository alertRepository, AlertConditionRepository alertConditionRepository, AlertMapper alertMapper, CategoryRepository categoryRepository, UserRepository userRepository, TransactionRepository transactionRepository, TransactionCategoriesRepository transactionCategoriesRepository) {
        this.alertRepository = alertRepository;
        this.alertConditionRepository = alertConditionRepository;
        this.alertMapper = alertMapper;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.transactionCategoriesRepository = transactionCategoriesRepository;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class AlertStateExtraction{
        List<AlertState> alertStates;
        boolean isBreached;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class AccountTransactionsMappedToAlerts{
        Map<UUID, List<TransactionEntity>> mapOfTransactions;
        // map alert id to maps of transaction - list of category ids
        Map<UUID, Map<UUID, List<UUID>>> mapOfTransactionCategories;
    }

    public boolean checkCondition(RuleType rule, BigDecimal firstVal, BigDecimal secondVal, BigDecimal thirdVal) {
        if (firstVal == null)
            throw new InvalidInputException("The value to evaluate needs to be non null.");

        boolean needsSecond = rule == RuleType.LESS_OR_EQUAL
                || rule == RuleType.LESS
                || rule == RuleType.EQUAL
                || rule == RuleType.BETWEEN;

        boolean needsThird = rule == RuleType.GREATER_OR_EQUAL
                || rule == RuleType.GREATER
                || rule == RuleType.BETWEEN;

        if (needsSecond && secondVal == null)
            throw new InvalidInputException("The lower/equal comparison value needs to be non null.");

        if (needsThird && thirdVal == null)
            throw new InvalidInputException("The upper comparison value needs to be non null.");

        return switch (rule) {
            case LESS_OR_EQUAL    -> firstVal.compareTo(secondVal) <= 0;
            case LESS              -> firstVal.compareTo(secondVal) < 0;
            case EQUAL              -> firstVal.compareTo(secondVal) == 0;
            case GREATER_OR_EQUAL -> firstVal.compareTo(thirdVal) >= 0;
            case GREATER            -> firstVal.compareTo(thirdVal) > 0;
            case BETWEEN            -> firstVal.compareTo(secondVal) >= 0 && firstVal.compareTo(thirdVal) <= 0;
        };
    }


    private List<AlertConditionInformation>  fetchListOfAlertConditionInformations(UUID alertId){
        List<AlertConditionEntity> alertConditionEntityList = alertConditionRepository.fetchAlertsConditions(alertId);
        return alertConditionEntityList.stream()
                .map(alertMapper::fromAlertConditionEntityToAlertConditionInformation)
                .toList();
    }

    private Map<UUID, List<AlertConditionInformation>> fetchAllAlertConditionsPerAlertInsideAccount(List<UUID> alertIds){
        List<AlertConditionEntity> alertConditionEntityList = alertConditionRepository.fetchAlertConditionsForAccount(alertIds);

        if(alertConditionEntityList.isEmpty())
            throw new IllegalStateException("Alert has to have one or more alert conditions.");

        return alertConditionEntityList.stream()
                .collect(Collectors.groupingBy(x->x.getAlert().getAlertId(),  Collectors.mapping(alertMapper::fromAlertConditionEntityToAlertConditionInformation,  Collectors.toList()
                )));
    }


    private Map<UUID, List<UUID>> fetchTransactionsThatAreUsedInTheAlert(List<UUID>  transactionIds, List<UUID> categoriesInTheAlert){
        // now get transaction ids that contain only alert categories
        List<TransactionCategoriesEntity>  transactionCategoriesEntityList = transactionCategoriesRepository.fetchAllTransactionCategoryIdsIn(transactionIds);
        // mapping transactionId to List of category Ids
         return transactionCategoriesEntityList.stream()
                .filter(t-> categoriesInTheAlert.contains(t.getId().getCategoryId()))
                .collect(Collectors.groupingBy(
                        t->t.getId().getTransactionId(),
                        Collectors.mapping(t->t.getId().getCategoryId(), Collectors.toList())
                ));
    }

    private AccountTransactionsMappedToAlerts fetchTransactionsUsedInAccountsAlerts(UUID accountId,
                                                                                    List<AlertEntity> alertEntities,
                                                                                    Map<UUID, List<UUID>> categoriesInTheAlert){

        List<TransactionEntity> transactionEntityList = transactionRepository.fetchAllAccountTransactions(accountId);
        List<TransactionCategoriesEntity> transactionCategoriesEntityList = transactionCategoriesRepository.
                fetchAllTransactionCategoryIdsIn(transactionEntityList.stream().map(TransactionEntity::getTransactionId).toList());


        Map<UUID, List<TransactionEntity>> mapOfTransactions = new HashMap<>();
        Map<UUID, Map<UUID, List<UUID>>> mapOfTransactionCategories = new HashMap<>();

        for(var alert: alertEntities){

            OffsetDateTime dateFrom = OffsetDateTime.of(
                    alert.getDateFrom(),
                    LocalTime.of(0, 0),
                    ZoneOffset.UTC
            );

            OffsetDateTime dateTo = OffsetDateTime.of(
                    alert.getDateTo(),
                    LocalTime.of(23, 59),
                    ZoneOffset.UTC
            );

            // transaction id - category ids
            Map<UUID, List<UUID>> uuidTransactionCategoriesEntityMap = transactionCategoriesEntityList.stream()
                    .filter(t-> categoriesInTheAlert.get(alert.getAlertId()).contains(t.getId().getCategoryId()))
                    .collect(Collectors.groupingBy(
                            t->t.getId().getTransactionId(),
                            Collectors.mapping(t->t.getId().getCategoryId(), Collectors.toList())
                    ));

            // list of transaction entities that are inside active alert date and contain alert categories
            List<TransactionEntity> listOfTargetTransactionIds = transactionEntityList.stream()
                    .filter(x-> !x.getCreatedAt().isAfter(dateTo) &&
                            !x.getCreatedAt().isBefore(dateFrom) &&
                            uuidTransactionCategoriesEntityMap.get(x.getTransactionId()) != null)
                    .toList();


            mapOfTransactions.put(alert.getAlertId(), listOfTargetTransactionIds);
            mapOfTransactionCategories.put(alert.getAlertId(), uuidTransactionCategoriesEntityMap);

        }

        return AccountTransactionsMappedToAlerts.builder()
                .mapOfTransactionCategories(mapOfTransactionCategories)
                .mapOfTransactions(mapOfTransactions)
                .build();
    }

    private List<TransactionEntity> getTransactionsFromToDate(AlertEntity alertEntity, UUID userId, AccountEntity accountEntity){
        OffsetDateTime dateFrom = OffsetDateTime.of(
                alertEntity.getDateFrom(),
                LocalTime.of(0, 0),
                ZoneOffset.UTC
        );

        OffsetDateTime dateTo = OffsetDateTime.of(
                alertEntity.getDateTo(),
                LocalTime.of(23, 59),
                ZoneOffset.UTC
        );

        // get all transactions inside the specified alert date, we need this to get only transactions inside the specified date alert is valid in
        return transactionRepository.fetchUserTransactionsInsideSpecifiedDate(userId, accountEntity.getAccountId(), dateFrom, dateTo);
    }


    private AlertStateExtraction extractAlertStates(List<UUID> categoriesInTheAlert,
                                                    Map<UUID, String> mapCategoryIdsToName,
                                                    List<TransactionEntity> transactionEntityList,
                                                    Map<UUID, List<UUID>> transactionsThatAreUsedInTheAlert,
                                                    List<AlertConditionInformation> alertConditionInformations){

        List<AlertState> alertStates = new ArrayList<>();
        List<Boolean> isConditionFullFilled = new ArrayList<>();

        for(var categoryId: categoriesInTheAlert){
            int count = 0;
            BigDecimal totalSum = new BigDecimal("0.00");

            String categoryName = mapCategoryIdsToName.get(categoryId);
            if(categoryName==null)
                throw  new NoSuchCategoryExistsException("The selected category in alert doesn't exist in database.");

            for(var transaction: transactionEntityList){
                // either the transaction doesn't contain none of the categories the alert has (null) or it doesnt contain current category (contains)
                List<UUID> categoriesForCheck = transactionsThatAreUsedInTheAlert.get(transaction.getTransactionId());
                if (categoriesForCheck == null || !categoriesForCheck.contains(categoryId))
                    continue;
                totalSum = totalSum.add(transaction.getAmount());
                count++;
            }

            // create alert state: gives us information about the category in alerts
            BigDecimal avg = count == 0 ? BigDecimal.ZERO : totalSum.divide(BigDecimal.valueOf(count), 2, RoundingMode.CEILING);
            AlertState alertState = AlertState.builder()
                    .amount(totalSum)
                    .categoryId(categoryId)
                    .categoryName(categoryName)
                    .numOfTransactions(count)
                    .avgPerTransaction(avg).build();

            alertStates.add(alertState);

            // now we need to check if this condition is met, if we have two categories in conditions we must check if all of them are fullfilled
            // also we cant have two same category ids in the same alert
            AlertConditionInformation alertCondition = alertConditionInformations.stream().filter(c -> c.getCategoryId().equals(categoryId)).findFirst()
                    .orElseThrow(() -> new IllegalStateException("No alert condition found for category " + categoryId));
            isConditionFullFilled.add(checkCondition(alertCondition.getRuleType(), totalSum, alertCondition.getLimitValueLowOrEqual(), alertCondition.getLimitValueHigh()));
        }

        return AlertStateExtraction.builder()
                .alertStates(alertStates)
                .isBreached(isConditionFullFilled.stream().allMatch(x->x.equals(Boolean.TRUE)))
                .build();

    }

    @Override
    public AlertInformation getAlert(UUID alertId, UUID userId) {
        // we fetch alert
        AlertEntity alertEntity = alertRepository.fetchAlert(alertId, userId).orElseThrow(()->new NoSuchAlertException("No such alert exists!"));

        // fetch alert conditions
        List<AlertConditionInformation> alertConditionInformations = fetchListOfAlertConditionInformations(alertId);

        // extract which categories are used from it
        List<UUID> categoriesInTheAlert = alertConditionInformations.stream().map(AlertConditionInformation::getCategoryId).toList();

        // check if the alert is active
        LocalDate dateNow = LocalDate.now();
        boolean isActive = !dateNow.isBefore(alertEntity.getDateFrom()) && !dateNow.isAfter(alertEntity.getDateTo());

        // get all transactions inside the specified alert date, we need this to get only transactions inside the specified date alert is valid in
        List<TransactionEntity> transactionEntityList = getTransactionsFromToDate(alertEntity, userId, alertEntity.getAccount());
        List<UUID> transactionIds = transactionEntityList.stream().map(TransactionEntity::getTransactionId).toList();

        // map of transaction - list category ids
        Map<UUID, List<UUID>> transactionCategoriesThatAreUsedInTheAlert = fetchTransactionsThatAreUsedInTheAlert(transactionIds, categoriesInTheAlert);

        // all categories available to user
        List<CategoryEntity> listOfAllCategories = categoryRepository.fetchUsersAndDefaultCategories(true,userId);
        Map<UUID, String> mapCategoryIdsToName = listOfAllCategories.stream().collect(Collectors.toMap(CategoryEntity::getCategoryId, CategoryEntity::getName));

        // extract list of alert states and if the conditions are all true
        AlertStateExtraction alertStateExtraction = extractAlertStates(categoriesInTheAlert, mapCategoryIdsToName, transactionEntityList, transactionCategoriesThatAreUsedInTheAlert, alertConditionInformations);

        return alertMapper.fromAlertEntityToAlertInformation(alertEntity, alertConditionInformations, alertStateExtraction.alertStates, alertStateExtraction.isBreached, isActive);
    }

    @Override
    public List<AlertInformation> getAccountAlerts(UUID accountId, UUID userId) {
        // we fetch alerts
        List<AlertEntity> alertEntities = alertRepository.fetchAlertsByAccountId(accountId, userId);
        if(alertEntities.isEmpty())
            return List.of();

        // fetch all alert conditions, map alertId - alertConditions
        Map<UUID, List<AlertConditionInformation>> mapAccountIdToAlertCondition = fetchAllAlertConditionsPerAlertInsideAccount(alertEntities.stream().map(AlertEntity::getAlertId).toList());

        // extract which categories are used from it, map alertId to all categoryIds, used for filtering relevant transactions
        Map<UUID, List<UUID>> categoriesInTheAlert = mapAccountIdToAlertCondition.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().map(AlertConditionInformation::getCategoryId).toList()));

        // fetch all account transaction and transaction categories and mapp them to alerts
        AccountTransactionsMappedToAlerts accountTransactionsMappedToAlerts  = fetchTransactionsUsedInAccountsAlerts(accountId, alertEntities, categoriesInTheAlert);
        Map<UUID, List<TransactionEntity>> mapOfTransactions = accountTransactionsMappedToAlerts.getMapOfTransactions();
        // map alert id to maps of transaction - list of category ids
        Map<UUID, Map<UUID, List<UUID>>> mapOfTransactionCategories = accountTransactionsMappedToAlerts.getMapOfTransactionCategories();

        // get all available categories to the user
        List<CategoryEntity> listOfAllCategories = categoryRepository.fetchUsersAndDefaultCategories(true,userId);
        Map<UUID, String> mapCategoryIdsToName = listOfAllCategories.stream().collect(Collectors.toMap(CategoryEntity::getCategoryId, CategoryEntity::getName));


        List<AlertInformation> responseList = new ArrayList<>();

        for(var alert: alertEntities){
            // check if the alert is active
            LocalDate dateNow = LocalDate.now();
            boolean isActive = !dateNow.isBefore(alert.getDateFrom()) && !dateNow.isAfter(alert.getDateTo());

            // get all transactions inside the specified alert date, we need this to get only transactions inside the specified date alert is valid in
            List<TransactionEntity> transactionEntityList = mapOfTransactions.get(alert.getAlertId());
            // map of transaction - list category ids
            Map<UUID, List<UUID>> transactionCategoriesThatAreUsedInTheAlert = mapOfTransactionCategories.get(alert.getAlertId());
            List<AlertConditionInformation> alertConditionInformations = mapAccountIdToAlertCondition.get(alert.getAlertId());

            // extract list of alert states and if the conditions are all true
            AlertStateExtraction alertStateExtraction = extractAlertStates(categoriesInTheAlert.get(alert.getAlertId()),
                    mapCategoryIdsToName,
                    transactionEntityList,
                    transactionCategoriesThatAreUsedInTheAlert,
                    alertConditionInformations);

            responseList.add(alertMapper.fromAlertEntityToAlertInformation(alert,
                    alertConditionInformations,
                    alertStateExtraction.alertStates,
                    alertStateExtraction.isBreached,
                    isActive));


        }


        return responseList;
    }

    @Override
    public AlertCreateUpdateDeleteResponse createAlert(AlertCreate alertCreate, UUID userId, UUID accountId) {
        return null;
    }

    @Override
    public AlertCreateUpdateDeleteResponse deleteAlert(UUID alertId, UUID userId) {
        return null;
    }

    @Override
    public AlertCreateUpdateDeleteResponse updateAlert(AlertUpdate alertUpdate, UUID alertId, UUID userId) {
        return null;
    }

}
