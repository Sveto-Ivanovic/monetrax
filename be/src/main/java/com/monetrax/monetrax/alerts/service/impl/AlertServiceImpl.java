package com.monetrax.monetrax.alerts.service.impl;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.accounts.exceptions.NoSuchAccountFound;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
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
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final AlertConditionRepository alertConditionRepository;
    private final AlertMapper alertMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionCategoriesRepository transactionCategoriesRepository;
    private final AccountRepository accountRepository;

    public AlertServiceImpl(AlertRepository alertRepository, AlertConditionRepository alertConditionRepository, AlertMapper alertMapper, CategoryRepository categoryRepository, UserRepository userRepository, TransactionRepository transactionRepository, TransactionCategoriesRepository transactionCategoriesRepository, AccountRepository accountRepository) {
        this.alertRepository = alertRepository;
        this.alertConditionRepository = alertConditionRepository;
        this.alertMapper = alertMapper;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.transactionCategoriesRepository = transactionCategoriesRepository;
        this.accountRepository = accountRepository;
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
        if (firstVal == null) {
            log.warn("checkCondition called with null value to evaluate: rule={}", rule);
            throw new InvalidInputException("The value to evaluate needs to be non null.");
        }

        boolean needsSecond = rule == RuleType.LESS_OR_EQUAL
                || rule == RuleType.LESS
                || rule == RuleType.EQUAL
                || rule == RuleType.BETWEEN;

        boolean needsThird = rule == RuleType.GREATER_OR_EQUAL
                || rule == RuleType.GREATER
                || rule == RuleType.BETWEEN;

        if (needsSecond && secondVal == null) {
            log.warn("checkCondition missing lower/equal comparison value: rule={}", rule);
            throw new InvalidInputException("The lower/equal comparison value needs to be non null.");
        }

        if (needsThird && thirdVal == null) {
            log.warn("checkCondition missing upper comparison value: rule={}", rule);
            throw new InvalidInputException("The upper comparison value needs to be non null.");
        }

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
        log.debug("Fetching alert conditions: alertId={}", alertId);
        List<AlertConditionEntity> alertConditionEntityList = alertConditionRepository.fetchAlertsConditions(alertId);
        log.debug("Fetched {} alert condition(s): alertId={}", alertConditionEntityList.size(), alertId);
        return alertConditionEntityList.stream()
                .map(alertMapper::fromAlertConditionEntityToAlertConditionInformation)
                .toList();
    }

    private Map<UUID, List<AlertConditionInformation>> fetchAllAlertConditionsPerAlertInsideAccount(List<UUID> alertIds){
        log.debug("Fetching alert conditions for {} alert(s)", alertIds.size());
        List<AlertConditionEntity> alertConditionEntityList = alertConditionRepository.fetchAlertConditionsForAccount(alertIds);

        if(alertConditionEntityList.isEmpty()) {
            log.error("No alert conditions found for alerts {}; every alert must have at least one condition", alertIds);
            throw new IllegalStateException("Alert has to have one or more alert conditions.");
        }

        log.debug("Fetched {} alert condition(s) for {} alert(s)", alertConditionEntityList.size(), alertIds.size());
        return alertConditionEntityList.stream()
                .collect(Collectors.groupingBy(x->x.getAlert().getAlertId(),  Collectors.mapping(alertMapper::fromAlertConditionEntityToAlertConditionInformation,  Collectors.toList()
                )));
    }


    private Map<UUID, List<UUID>> fetchTransactionsThatAreUsedInTheAlert(List<UUID>  transactionIds, List<UUID> categoriesInTheAlert){
        // now get transaction ids that contain only alert categories
        List<TransactionCategoriesEntity>  transactionCategoriesEntityList = transactionCategoriesRepository.fetchAllTransactionCategoryIdsIn(transactionIds);
        // mapping transactionId to List of category Ids
        Map<UUID, List<UUID>> result = transactionCategoriesEntityList.stream()
                .filter(t-> categoriesInTheAlert.contains(t.getId().getCategoryId()))
                .collect(Collectors.groupingBy(
                        t->t.getId().getTransactionId(),
                        Collectors.mapping(t->t.getId().getCategoryId(), Collectors.toList())
                ));
        log.debug("Matched {} of {} transaction(s) to alert categories", result.size(), transactionIds.size());
        return result;
    }

    private AccountTransactionsMappedToAlerts fetchTransactionsUsedInAccountsAlerts(UUID accountId,
                                                                                    List<AlertEntity> alertEntities,
                                                                                    Map<UUID, List<UUID>> categoriesInTheAlert){

        log.debug("Fetching account transactions for alerts: accountId={}, alertCount={}", accountId, alertEntities.size());
        List<TransactionEntity> transactionEntityList = transactionRepository.fetchAllAccountTransactions(accountId);
        List<TransactionCategoriesEntity> transactionCategoriesEntityList = transactionCategoriesRepository.
                fetchAllTransactionCategoryIdsIn(transactionEntityList.stream().map(TransactionEntity::getTransactionId).toList());
        log.debug("Fetched {} transaction(s) and {} transaction-category link(s): accountId={}",
                transactionEntityList.size(), transactionCategoriesEntityList.size(), accountId);


        Map<UUID, List<TransactionEntity>> mapOfTransactions = new HashMap<>();
        Map<UUID, Map<UUID, List<UUID>>> mapOfTransactionCategories = new HashMap<>();

        for(var alert: alertEntities){

            OffsetDateTime dateFrom = OffsetDateTime.of(
                    alert.getDateFrom(),
                    LocalTime.of(0, 0),
                    ZoneOffset.UTC
            );

            LocalDate minDate = LocalDate.now(ZoneOffset.UTC)
                    .isBefore(alert.getDateTo())
                    ? LocalDate.now(ZoneOffset.UTC)
                    : alert.getDateTo();


            OffsetDateTime dateTo = OffsetDateTime.of(
                    minDate,
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

            log.debug("Alert {} matched {} transaction(s) in window {} - {}",
                    alert.getAlertId(), listOfTargetTransactionIds.size(), dateFrom, dateTo);

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

        LocalDate minDate = LocalDate.now(ZoneOffset.UTC)
                .isBefore(alertEntity.getDateTo())
                ? LocalDate.now(ZoneOffset.UTC)
                : alertEntity.getDateTo();

        OffsetDateTime dateTo = OffsetDateTime.of(
                minDate,
                LocalTime.of(23, 59),
                ZoneOffset.UTC
        );

        log.debug("Fetching transactions in alert window: alertId={}, accountId={}, from={}, to={}",
                alertEntity.getAlertId(), accountEntity.getAccountId(), dateFrom, dateTo);

        // get all transactions inside the specified alert date, we need this to get only transactions inside the specified date alert is valid in
        return transactionRepository.fetchUserTransactionsInsideSpecifiedDate(userId, accountEntity.getAccountId(), dateFrom, dateTo);
    }


    private AlertStateExtraction extractAlertStates(List<UUID> categoriesInTheAlert,
                                                    Map<UUID, String> mapCategoryIdsToName,
                                                    List<TransactionEntity> transactionEntityList,
                                                    Map<UUID, List<UUID>> transactionsThatAreUsedInTheAlert,
                                                    List<AlertConditionInformation> alertConditionInformations){

        log.debug("Extracting alert states: categories={}, transactions={}", categoriesInTheAlert.size(), transactionEntityList.size());

        List<AlertState> alertStates = new ArrayList<>();
        List<Boolean> isConditionFullFilled = new ArrayList<>();

        for(var categoryId: categoriesInTheAlert){
            int count = 0;
            BigDecimal totalSum = new BigDecimal("0.00");

            String categoryName = mapCategoryIdsToName.get(categoryId);
            if(categoryName==null) {
                log.error("Category referenced by alert does not exist: categoryId={}", categoryId);
                throw  new NoSuchCategoryExistsException("The selected category in alert doesn't exist in database.");
            }

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
                    .orElseThrow(() -> {
                        log.error("No alert condition found for category: categoryId={}", categoryId);
                        return new IllegalStateException("No alert condition found for category " + categoryId);
                    });
            boolean conditionMet = checkCondition(alertCondition.getRuleType(), totalSum, alertCondition.getLimitValueLowOrEqual(), alertCondition.getLimitValueHigh());
            log.debug("Condition evaluated: categoryId={}, rule={}, transactions={}, conditionMet={}",
                    categoryId, alertCondition.getRuleType(), count, conditionMet);
            isConditionFullFilled.add(conditionMet);
        }

        return AlertStateExtraction.builder()
                .alertStates(alertStates)
                .isBreached(isConditionFullFilled.stream().allMatch(x->x.equals(Boolean.TRUE)))
                .build();

    }


    private List<AlertConditionEntity> saveFiltersForAlerts(List<AlertEntity> listOfAlerts,
                                                            List<AlertConditionCreation> filtersToCreate,
                                                            Map<UUID, CategoryEntity> categoryMap,
                                                            List<UUID> listOfAllCategoryIds
    ){
        log.debug("Validating and saving {} filter(s) for {} alert(s)", filtersToCreate.size(), listOfAlerts.size());
        // Note the alerts need to be saved first
        // check validity of filters
        Set<UUID> categoryIds = new HashSet<>();
        filtersToCreate.forEach(x->{
            UUID categoryId = x.getCategoryId();

            if (!categoryIds.add(categoryId)) {
                log.warn("Duplicate category in filters: categoryId={}", categoryId);
                throw new IllegalStateException(
                        "Duplicate category ID in filters: " + categoryId
                );
            }

            if (!listOfAllCategoryIds.contains(categoryId)) {
                log.warn("Filter references invalid category: categoryId={}", categoryId);
                throw new IllegalStateException(
                        "The category selected in the filter is invalid."
                );
            }

            RuleType rule = x.getRuleType();
            boolean needLowEqual = rule == RuleType.LESS_OR_EQUAL
                    || rule == RuleType.LESS
                    || rule == RuleType.EQUAL
                    || rule == RuleType.BETWEEN;

            boolean needHigh = rule == RuleType.GREATER_OR_EQUAL
                    || rule == RuleType.GREATER
                    || rule == RuleType.BETWEEN;

            if (needLowEqual && x.getLimitValueLowOrEqual() == null) {
                log.warn("Filter missing lower/equal value: categoryId={}, rule={}", categoryId, rule);
                throw new InvalidInputException("The lower/equal comparison value needs to be non null.");
            }

            if (needHigh && x.getLimitValueHigh() == null) {
                log.warn("Filter missing upper value: categoryId={}, rule={}", categoryId, rule);
                throw new InvalidInputException("The upper comparison value needs to be non null.");
            }

        });


        List<AlertConditionEntity> alertConditionEntityList = new ArrayList<>();

        for(var alert: listOfAlerts){
            // here we will accumulate filters for the modes
            for(var alertCondition: filtersToCreate){
                alertConditionEntityList.add(
                        alertMapper.fromAlertConditionCreateToAlertConditionEntity(alertCondition, alert, categoryMap.get(alertCondition.getCategoryId()))
                );
            }
        }
        List<AlertConditionEntity> saved = alertConditionRepository.saveAll(alertConditionEntityList);
        log.debug("Saved {} alert condition(s)", saved.size());
        return saved;
    }


    private List<AlertEntity> createAlertsWithAlertCreationOptions(AlertEntity alertEntity,
                                                                   AlertRecurrenceRule creationOption) {

        log.debug("Generating recurring alerts: ruleType={}, occurrences={}, recurrenceNum={}",
                creationOption.getRuleType(), creationOption.getNumberOfOccurrences(), creationOption.getRecurrenceNum());

        List<AlertEntity> alertEntityList = new ArrayList<>();
        alertEntityList.add(alertEntity);
        String originalName = alertEntity.getName();

        for(int i = 1; i<creationOption.getNumberOfOccurrences()+1; i++){
            LocalDate dateFrom = alertEntityList.get(i-1).getDateFrom();
            LocalDate dateTo = alertEntityList.get(i-1).getDateTo();
            String currentName = originalName + " iteration_" + i;
            int recurrenceNum = creationOption.getRecurrenceNum() > 0 ? creationOption.getRecurrenceNum(): 1;

            LocalDate newDateFrom = switch (creationOption.getRuleType()){
                case EVERY_WEEK -> dateFrom.plusWeeks(recurrenceNum);
                case EVERY_MONTH -> {
                    boolean isLastDayOfMonth = dateFrom.getDayOfMonth() == dateFrom.lengthOfMonth();
                    yield isLastDayOfMonth
                            ? dateFrom.plusMonths(recurrenceNum).with(TemporalAdjusters.lastDayOfMonth())
                            : dateFrom.plusMonths(recurrenceNum);
                }
                case EVERY_YEAR -> dateFrom.plusYears(recurrenceNum);
                case EVERY_SPAN -> {
                    long daySpan = ChronoUnit.DAYS.between(dateFrom, dateTo);
                    yield dateFrom.plusDays(daySpan+1);
                }
            };


            LocalDate newDateTo = switch (creationOption.getRuleType()){
                case EVERY_WEEK -> dateTo.plusWeeks(recurrenceNum);
                case EVERY_MONTH -> {
                    boolean isLastDayOfMonth = dateTo.getDayOfMonth() == dateTo.lengthOfMonth();
                    yield isLastDayOfMonth
                            ? dateTo.plusMonths(recurrenceNum).with(TemporalAdjusters.lastDayOfMonth())
                            : dateTo.plusMonths(recurrenceNum);
                }
                case EVERY_YEAR -> dateTo.plusYears(recurrenceNum);
                case EVERY_SPAN -> {
                    long daySpan = ChronoUnit.DAYS.between(dateFrom, dateTo);
                    yield dateTo.plusDays(daySpan+1);
                }
            };

            log.trace("Recurrence iteration {}: {} - {}", i, newDateFrom, newDateTo);

            alertEntityList.add(AlertEntity.builder()
                    .name(currentName)
                    .account(alertEntity.getAccount())
                    .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                    .dateFrom(newDateFrom)
                    .dateTo(newDateTo)
                    .description(alertEntity.getDescription())
                    .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                    .user(alertEntity.getUser())
                    .build()
            );
        }
        List<AlertEntity> saved = alertRepository.saveAll(alertEntityList);
        log.debug("Saved {} recurring alert(s)", saved.size());
        return saved;
    }

    @Override
    public AlertInformation getAlert(UUID alertId, UUID userId) {
        log.debug("Fetching alert: alertId={}, userId={}", alertId, userId);
        // we fetch alert
        AlertEntity alertEntity = alertRepository.fetchAlert(alertId, userId).orElseThrow(()->{
            log.warn("Alert not found: alertId={}, userId={}", alertId, userId);
            return new NoSuchAlertException("No such alert exists!");
        });

        // fetch alert conditions
        List<AlertConditionInformation> alertConditionInformations = fetchListOfAlertConditionInformations(alertId);

        // extract which categories are used from it
        List<UUID> categoriesInTheAlert = alertConditionInformations.stream().map(AlertConditionInformation::getCategoryId).toList();

        // check if the alert is active
        LocalDate dateNow = LocalDate.now(ZoneOffset.UTC);
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

        log.info("Alert evaluated: alertId={}, userId={}, active={}, breached={}",
                alertId, userId, isActive, alertStateExtraction.isBreached);

        return alertMapper.fromAlertEntityToAlertInformation(alertEntity, alertConditionInformations, alertStateExtraction.alertStates, alertStateExtraction.isBreached, isActive);
    }

    @Override
    public List<AlertInformation> getAccountAlerts(UUID accountId, UUID userId, boolean includeOnlyActiveAlerts) {
        log.debug("Fetching account alerts: accountId={}, userId={}, onlyActive={}", accountId, userId, includeOnlyActiveAlerts);
        // we fetch alerts
        List<AlertEntity> alertEntities = includeOnlyActiveAlerts ?
                alertRepository.fetchAlertsByAccountIdThatAreActive(accountId, userId, LocalDate.now(ZoneOffset.UTC)) :
                alertRepository.fetchAlertsByAccountId(accountId, userId);
        if(alertEntities.isEmpty()) {
            log.debug("No alerts found: accountId={}, userId={}, onlyActive={}", accountId, userId, includeOnlyActiveAlerts);
            return List.of();
        }

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
            LocalDate dateNow = LocalDate.now(ZoneOffset.UTC);
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

            log.debug("Alert evaluated: alertId={}, active={}, breached={}", alert.getAlertId(), isActive, alertStateExtraction.isBreached);

            responseList.add(alertMapper.fromAlertEntityToAlertInformation(alert,
                    alertConditionInformations,
                    alertStateExtraction.alertStates,
                    alertStateExtraction.isBreached,
                    isActive));


        }

        log.info("Fetched and evaluated {} alert(s): accountId={}, userId={}", responseList.size(), accountId, userId);
        return responseList;
    }

    @Override
    @Transactional
    public AlertCreateUpdateDeleteResponse createAlert(AlertCreate alertCreate, UUID userId, UUID accountId) {
        log.info("Creating alert: userId={}, accountId={}", userId, accountId);
        if(alertCreate.getFiltersToCreate().isEmpty()) {
            log.warn("Alert creation rejected, no filters provided: userId={}, accountId={}", userId, accountId);
            throw new IllegalArgumentException("Number of filters must be one or more.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(()->{
            log.warn("Cannot create alert, user not found: userId={}", userId);
            return new NoSuchUserExistsException("No user with id: "+ userId);
        });
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(()->{
            log.warn("Cannot create alert, account not found: userId={}, accountId={}", userId, accountId);
            return new NoSuchAccountFound("No such account exists!");
        });


        // get all available categories to the user
        List<CategoryEntity> listOfAllCategories = categoryRepository.fetchUsersAndDefaultCategories(true,userId);
        List<UUID> listOfAllCategoryIds = listOfAllCategories.stream().map(CategoryEntity::getCategoryId).toList();
        // categoryId to CategoryEntities
        Map<UUID, CategoryEntity> categoryMap = listOfAllCategories.stream()
                .collect(Collectors.toMap(CategoryEntity::getCategoryId, x->x));


        AlertEntity alert = alertMapper.fromAlertCreateToAlertEntity(alertCreate, account, user);
        if(alert.getDateFrom().isAfter(alert.getDateTo())) {
            log.warn("Alert creation rejected, start date after end date: userId={}, accountId={}", userId, accountId);
            throw  new IllegalArgumentException("The begging date must come before the end date.");
        }

        if(alertCreate.getAlertRecurrenceRule()!=null){
            // here logic to add the multi alertEntities
            List<AlertEntity> alertEntityListSaved = createAlertsWithAlertCreationOptions(alert, alertCreate.getAlertRecurrenceRule());
            List<AlertConditionEntity> alertConditionEntityList =
                    saveFiltersForAlerts(alertEntityListSaved, alertCreate.getFiltersToCreate(), categoryMap, listOfAllCategoryIds);
            log.info("Created {} recurring alert(s) with {} condition(s): userId={}, accountId={}",
                    alertEntityListSaved.size(), alertConditionEntityList.size(), userId, accountId);
            return new AlertCreateUpdateDeleteResponse("Successfully created %d alerts.".formatted(alertEntityListSaved.size()), null);
        }
        else {
            AlertEntity alertEntitySaved = alertRepository.save(alert);
            List<AlertConditionEntity> alertConditionEntityList =
                    saveFiltersForAlerts(List.of(alertEntitySaved), alertCreate.getFiltersToCreate(), categoryMap, listOfAllCategoryIds);
            log.info("Created alert with {} condition(s): alertId={}, userId={}, accountId={}",
                    alertConditionEntityList.size(), alertEntitySaved.getAlertId(), userId, accountId);
            return new AlertCreateUpdateDeleteResponse("Successfully created alert.", alertEntitySaved.getAlertId());
        }

    }

    @Override
    @Transactional
    public AlertCreateUpdateDeleteResponse updateAlert(AlertUpdate alertUpdate, UUID alertId, UUID userId) {
        log.info("Updating alert: alertId={}, userId={}", alertId, userId);

        AlertEntity alertEntity = alertRepository.fetchAlert(alertId, userId).orElseThrow(()->{
            log.warn("Cannot update, alert not found: alertId={}, userId={}", alertId, userId);
            return new NoSuchAlertException("No such alert exists!");
        });

        if (alertUpdate.getDescription() == null && alertUpdate.getName() == null && alertUpdate.getFiltersToCreate() == null) {
            log.warn("Alert update rejected, no fields provided: alertId={}, userId={}", alertId, userId);
            throw new InvalidInputException("At least one field must be provided for update");
        }

        log.debug("Updating fields for alertId={}: name={}, description={}, filters={}",
                alertId,
                alertUpdate.getName() != null,
                alertUpdate.getDescription() != null,
                alertUpdate.getFiltersToCreate() != null);

        Optional.ofNullable(alertUpdate.getDescription()).ifPresent(alertEntity::setDescription);
        Optional.ofNullable(alertUpdate.getName()).ifPresent(alertEntity::setName);

        alertEntity.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        var alertEntitySaved = alertRepository.save(alertEntity);

        if(!(alertUpdate.getFiltersToCreate() == null)){
            if(alertUpdate.getFiltersToCreate().isEmpty()) {
                log.warn("Alert update rejected, empty filter list: alertId={}, userId={}", alertId, userId);
                throw new IllegalArgumentException("Number of filters must be one or more.");
            }

            List<AlertConditionEntity> alertConditionEntityListToDelete = alertConditionRepository.fetchAlertsConditions(alertEntity.getAlertId());

            alertConditionRepository.deleteAll(alertConditionEntityListToDelete);
            log.debug("Deleted {} old alert condition(s): alertId={}", alertConditionEntityListToDelete.size(), alertId);
            // get all available categories to the user
            List<CategoryEntity> listOfAllCategories = categoryRepository.fetchUsersAndDefaultCategories(true,userId);
            List<UUID> listOfAllCategoryIds = listOfAllCategories.stream().map(CategoryEntity::getCategoryId).toList();
            // categoryId to CategoryEntities
            Map<UUID, CategoryEntity> categoryMap = listOfAllCategories.stream()
                    .collect(Collectors.toMap(CategoryEntity::getCategoryId, x->x));

            List<AlertConditionEntity> alertConditionEntityList =
                    saveFiltersForAlerts(List.of(alertEntitySaved), alertUpdate.getFiltersToCreate(), categoryMap, listOfAllCategoryIds);
            log.debug("Replaced alert conditions with {} new condition(s): alertId={}", alertConditionEntityList.size(), alertId);
        }

        log.info("Alert updated: alertId={}, userId={}", alertId, userId);
        return new AlertCreateUpdateDeleteResponse("Successfully updated alert.", alertEntity.getAlertId());
    }

    @Override
    @Transactional
    public AlertCreateUpdateDeleteResponse deleteAlert(UUID alertId, UUID userId) {
        log.info("Deleting alert: alertId={}, userId={}", alertId, userId);

        AlertEntity alertEntity = alertRepository.fetchAlert(alertId, userId).orElseThrow(()->{
            log.warn("Cannot delete, alert not found: alertId={}, userId={}", alertId, userId);
            return new NoSuchAlertException("No such alert exists!");
        });

        List<AlertConditionEntity> alertConditionEntityList = alertConditionRepository.fetchAlertsConditions(alertEntity.getAlertId());

        alertConditionRepository.deleteAll(alertConditionEntityList);
        alertRepository.delete(alertEntity);

        log.info("Alert deleted along with {} condition(s): alertId={}, userId={}", alertConditionEntityList.size(), alertId, userId);
        return new AlertCreateUpdateDeleteResponse("Successfully deleted alert.", alertEntity.getAlertId());
    }


}