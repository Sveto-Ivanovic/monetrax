package com.monetrax.monetrax.analytics.service.impl;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.accounts.exceptions.NoSuchAccountFound;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.alerts.exceptions.InvalidInputException;
import com.monetrax.monetrax.analytics.dto.*;
import com.monetrax.monetrax.analytics.service.AnalyticsService;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.transactions.dto.TransactionInformationPart;
import com.monetrax.monetrax.transactions.entity.TransactionCategoriesEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import com.monetrax.monetrax.transactions.repository.TransactionCategoriesRepository;
import com.monetrax.monetrax.transactions.repository.TransactionLineItemsRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionCategoriesRepository transactionCategoriesRepository;
    private final TransactionLineItemsRepository transactionLineItemsRepository;

    public AnalyticsServiceImpl(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, TransactionCategoriesRepository transactionCategoriesRepository, TransactionLineItemsRepository transactionLineItemsRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionCategoriesRepository = transactionCategoriesRepository;
        this.transactionLineItemsRepository = transactionLineItemsRepository;
    }

    private List<MonthlyIncome> fillMissingMonthsIncome(List<MonthlyBalance> rows,
                                                  YearMonth start,
                                                  YearMonth end) {

        Map<YearMonth, MonthlyIncome> yearMonthMonthlyIncomeMap = rows.stream()
                .collect(Collectors.toMap(x-> {
                    String stringYM = x.getMonth();
                    int year = Integer.parseInt(stringYM.substring(0, 4));
                    int month = Integer.parseInt(stringYM.substring(5));
                    return YearMonth.of(year, month);

                }, x-> MonthlyIncome.builder().totalIncome(x.getAmount()).month(x.getMonth()).currency(x.getCurrency()).build() ));

        List<MonthlyIncome> rowsToReturn = new ArrayList<>();
        for(YearMonth startMonth = start; !startMonth.isAfter(end); startMonth=startMonth.plusMonths(1)){
            rowsToReturn.add(yearMonthMonthlyIncomeMap.getOrDefault(startMonth, MonthlyIncome.builder().currency("EUR")
                    .month(startMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .totalIncome(BigDecimal.ZERO).build()));
        }

        Collections.reverse(rowsToReturn);
        return rowsToReturn;
    }


    private List<MonthlyExpenses> fillMissingMonthsExpense(List<MonthlyBalance> rows,
                                                        YearMonth start,
                                                        YearMonth end) {

        Map<YearMonth, MonthlyExpenses> yearMonthMonthlyExpensesMap = rows.stream()
                .collect(Collectors.toMap(x-> {
                    String stringYM = x.getMonth();
                    int year = Integer.parseInt(stringYM.substring(0, 4));
                    int month = Integer.parseInt(stringYM.substring(5));
                    return YearMonth.of(year, month);

                }, x-> MonthlyExpenses.builder().totalExpenses(x.getAmount()).currency(x.getCurrency()).month(x.getMonth()).build() ));

        List<MonthlyExpenses> rowsToReturn = new ArrayList<>();
        for(YearMonth startMonth = start; !startMonth.isAfter(end); startMonth=startMonth.plusMonths(1)){
            rowsToReturn.add(yearMonthMonthlyExpensesMap.getOrDefault(startMonth, MonthlyExpenses.builder().currency("EUR")
                    .month(startMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .totalExpenses(BigDecimal.ZERO).build()));
        }
        Collections.reverse(rowsToReturn);
        return rowsToReturn;
    }


    private List<TotalBalanceMonthly> fillTotalBalanceBasedOnCurrentState(BigDecimal currentBalance,
                                                                          List<MonthlyIncome> rowsIncome,
                                                                          List<MonthlyExpenses> rowsExpenses,
                                                                          YearMonth start,
                                                                          YearMonth end
                                                                          ){
        // this function expects that the two lists are of the same length and that the two lists posses same months and in the order latest-> oldest
        // won't do checks here



        Map<YearMonth, MonthlyExpenses> yearMonthMonthlyExpensesMap = rowsExpenses.stream()
                .collect(Collectors.toMap(x-> {
                    String stringYM = x.getMonth();
                    int year = Integer.parseInt(stringYM.substring(0, 4));
                    int month = Integer.parseInt(stringYM.substring(5));
                    return YearMonth.of(year, month);

                }, x->x ));

        Map<YearMonth, MonthlyIncome> yearMonthMonthlyIncomeMap = rowsIncome.stream()
                .collect(Collectors.toMap(x-> {
                    String stringYM = x.getMonth();
                    int year = Integer.parseInt(stringYM.substring(0, 4));
                    int month = Integer.parseInt(stringYM.substring(5));
                    return YearMonth.of(year, month);

                }, x->x ));

        List<TotalBalanceMonthly> rowsToReturn = new ArrayList<>();
        BigDecimal stateVal = BigDecimal.ZERO.add(currentBalance);
        for(YearMonth endMonth = end; !endMonth.isBefore(start); endMonth=endMonth.minusMonths(1)){
            BigDecimal income = yearMonthMonthlyIncomeMap.containsKey(endMonth.plusMonths(1)) ? yearMonthMonthlyIncomeMap.get(endMonth.plusMonths(1)).getTotalIncome(): BigDecimal.ZERO;
            BigDecimal expense = yearMonthMonthlyExpensesMap.containsKey(endMonth.plusMonths(1)) ? yearMonthMonthlyExpensesMap.get(endMonth.plusMonths(1)).getTotalExpenses(): BigDecimal.ZERO;
            stateVal = stateVal.add(expense).subtract(income);

            rowsToReturn
                    .add(TotalBalanceMonthly.builder().currency("EUR")
                    .month(endMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .totalBalance(stateVal).build());
        }

        return rowsToReturn;
    }


    private List<String> buildBuckets(OffsetDateTime from, OffsetDateTime to, DateTimeFormatter fmt) {
        List<String> buckets = new ArrayList<>();

        for(OffsetDateTime fromDay = from; !fromDay.isAfter(to); fromDay = fromDay.plusDays(1))
                buckets.add(fromDay.format(fmt));


        if(buckets.size()>5475){
            throw new InvalidInputException("Maximum date range is 15 years;");
        }

        return buckets;
    }


    private List<AnalyticsResponse.CategoryKindAmount> aggregateByKind(
            List<TransactionEntity> transactionEntityList,
            boolean isIncome,
            DateTimeFormatter formatter,
            List<String> buckets

    ){
        Map<String, BigDecimal> sums;
        if(isIncome)
            sums =  transactionEntityList.stream()
                .filter(x->x.getCategoryType().equals(CategoryKind.INCOME) || x.getCategoryType().equals(CategoryKind.ADJUSTMENT_PLUS) || x.getCategoryType().equals(CategoryKind.TRANSFER_FROM))
                .collect(
                        Collectors.groupingBy(
                                x -> x.getCreatedAt().toLocalDate().format(formatter),
                                Collectors.mapping(TransactionEntity::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                        )
                );
        else
            sums =  transactionEntityList.stream()
                    .filter(x->x.getCategoryType().equals(CategoryKind.EXPENSE) || x.getCategoryType().equals(CategoryKind.ADJUSTMENT_MINUS) || x.getCategoryType().equals(CategoryKind.TRANSFER_TO))
                    .collect(
                            Collectors.groupingBy(
                                    x -> x.getCreatedAt().toLocalDate().format(formatter),
                                    Collectors.mapping(TransactionEntity::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                            )
                    );

        return buckets.stream()
                        .map(x-> AnalyticsResponse.CategoryKindAmount.builder()
                                .amount(sums.getOrDefault(x, BigDecimal.ZERO)).categoryKind(isIncome? CategoryKind.INCOME : CategoryKind.EXPENSE)
                                .xAxisData(x)
                                .build()).toList();

    }

    private Map<String, List<AnalyticsResponse.CategoryState>> mapDatesToCategoryStates( Set<CategoryEntity> categoryEntitiesSet,
                                                                                         Map<UUID, List<TransactionCategoriesEntity>> transactionCategoriesEntityMap,
                                                                                         List<String> buckets) {

        Map<String, List<AnalyticsResponse.CategoryState>> resp = new LinkedHashMap<>();
        for(String bucket: buckets){
            List<AnalyticsResponse.CategoryState> categoryStateList = new ArrayList<>();
            for(CategoryEntity categoryEntity: categoryEntitiesSet){

                UUID categoryId = categoryEntity.getCategoryId();
                String name = categoryEntity.getName();

                // get all transactions that use this category
                List<TransactionCategoriesEntity> transactionCategoriesEntityList1 = transactionCategoriesEntityMap.get(categoryId).stream()
                        .filter(x->x.getTransaction().getCreatedAt().toLocalDate().toString().equals(bucket)).toList();


                if (transactionCategoriesEntityList1.isEmpty()) {
                    throw new IllegalStateException(
                            "No transactions found for category: " + categoryId
                    );
                }

                BigDecimal amountT = transactionCategoriesEntityList1.stream()
                        .map(x->x.getTransaction().getAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                categoryStateList.add(AnalyticsResponse.CategoryState.builder().amount(amountT).categoryId(categoryId).categoryName(name).build());
            }

            resp.put(bucket, categoryStateList);

        }


        return resp;
    }



    private Map<String, List<AnalyticsResponse.Products>> mapDatesToProductStates( List<TransactionLineItemsEntity> transactionLineItemsEntities,
                                                                                         List<String> buckets) {

        Map<String, List<AnalyticsResponse.Products>> resp = new LinkedHashMap<>();
        for(String bucket: buckets){
            List<AnalyticsResponse.Products> productStateList = new ArrayList<>();
            Set<String> uniqueNamesOfProducts = transactionLineItemsEntities.stream().map(TransactionLineItemsEntity::getProductName).collect(Collectors.toSet());

            for(var prodName: uniqueNamesOfProducts){
                BigDecimal sumOfCost = transactionLineItemsEntities.stream().filter(x->x.getProductName().equals(prodName) && x.getTransaction()
                                .getCreatedAt().toLocalDate().toString().equals(bucket))
                        .map(TransactionLineItemsEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                int numberOfPurchases =(int)transactionLineItemsEntities.stream().filter(x->x.getProductName().equals(prodName) && x.getTransaction()
                        .getCreatedAt().toLocalDate().toString().equals(bucket)).count();
                productStateList.add(AnalyticsResponse.Products.builder().productName(prodName).amount(sumOfCost).numberOfPurchases(numberOfPurchases).build());
            }


            resp.put(bucket, productStateList);

        }


        return resp;
    }

    @Override
    public AnalyticsResponse getAnalytics(AnalyticsRequest analyticsRequest, UUID userId) {

        if(analyticsRequest.getDateFrom().isAfter(analyticsRequest.getDateTo())){
            throw new InvalidInputException("The date from needs to be before date to.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("No user found with id={}", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });

        // get accounts of interest
        AccountEntity accountEntity = accountRepository.getAccount(userId, analyticsRequest.getAccountId()).orElseThrow(()->{
            log.warn("No account found with id={}", analyticsRequest.getAccountId());
            return new NoSuchAccountFound("No account with id: " + analyticsRequest.getAccountId());
        });


        // extract the variables of interest
        OffsetDateTime dateFrom = analyticsRequest.getDateFrom();
        OffsetDateTime dateTo = analyticsRequest.getDateTo();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> buckets = buildBuckets(dateFrom, dateTo, formatter);

        UUID accountId = accountEntity.getAccountId();
        List<TransactionEntity> transactionEntityList = transactionRepository.fetchUserTransactionsInsideSpecifiedDate(userId, accountId, dateFrom, dateTo);

        if(transactionEntityList.isEmpty())
                return AnalyticsResponse.builder().accountId(accountId).accountName(accountEntity.getName())
                        .state(accountEntity.getCurrentBalance()).currency(accountEntity.getCurrency())
                        .incomeTotal(BigDecimal.ZERO).expenseTotal(BigDecimal.ZERO).net(BigDecimal.ZERO)
                        .income(new ArrayList<>()).expense(new ArrayList<>())
                        .categoryStateListExpense(new ArrayList<>()).categoryStateListIncome(new ArrayList<>()).transactionInformationPartList(new ArrayList<>()).productsList(new ArrayList<>())
                        .mapDateAmountIncome(new LinkedHashMap<>()).mapDateAmountExpense(new LinkedHashMap<>()).mapDateAmountNet(new LinkedHashMap<>())
                        .stringListMapCategoriesByDate(new LinkedHashMap<>()).stringListMapProductByDate(new LinkedHashMap<>()).build();


        // all transaction ids used
        List<UUID> transactionIds = transactionEntityList.stream().map(TransactionEntity::getTransactionId).toList();
        List<TransactionCategoriesEntity> transactionCategoriesEntityList = transactionCategoriesRepository.fetchAllTransactionCategoryIdsInWithJoin(transactionIds);

        // map category ids to list of transactionCategories
        Map<UUID, List<TransactionCategoriesEntity>> transactionCategoriesEntityMap = transactionCategoriesEntityList.stream()
                    .collect(Collectors.groupingBy( x->x.getId().getCategoryId(), Collectors.mapping(x->x, Collectors.toList()) ));

        // map transaction ids to the list of category names
        Map<UUID, List<String>> mapTransactionIdsToListOfCategoryNames = transactionCategoriesEntityList.stream()
                .collect(Collectors.groupingBy(x->x.getTransaction().getTransactionId(), Collectors.mapping(x->x.getCategory().getName(), Collectors.toList())));

        // Unique Categories used
        Set<CategoryEntity> categoryEntitiesSet = transactionCategoriesEntityList.stream().map(TransactionCategoriesEntity::getCategory).collect(Collectors.toSet());

        // the category type vals, first 5 fields in analytics !!!
        List<AnalyticsResponse.CategoryKindAmount> income = aggregateByKind(transactionEntityList, true, formatter, buckets);
        List<AnalyticsResponse.CategoryKindAmount> expense = aggregateByKind(transactionEntityList, false, formatter, buckets);
        BigDecimal incomeTotal = income.stream().map(AnalyticsResponse.CategoryKindAmount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expenseTotal = expense.stream().map(AnalyticsResponse.CategoryKindAmount::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal net = incomeTotal.subtract(expenseTotal);

        // we go through unique categories used here, nexy 2 fields !!!
        List<AnalyticsResponse.CategoryState> categoryStateListExpense = new ArrayList<>();
        List<AnalyticsResponse.CategoryState> categoryStateListIncome= new ArrayList<>();

        for(var category : categoryEntitiesSet){
            UUID categoryId = category.getCategoryId();
            String name = category.getName();

            // get all transactions that use this category
            List<TransactionCategoriesEntity> transactionCategoriesEntityList1 = transactionCategoriesEntityMap.get(categoryId);
            BigDecimal amountT = transactionCategoriesEntityList1.stream()
                    .map(x->x.getTransaction().getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if(category.getCategoryType() == CategoryKind.EXPENSE || category.getCategoryType() == CategoryKind.ADJUSTMENT_MINUS || category.getCategoryType() == CategoryKind.TRANSFER_TO)
                categoryStateListExpense.add(AnalyticsResponse.CategoryState.builder().amount(amountT).categoryId(categoryId).categoryName(name).build());
            else
                categoryStateListIncome.add(AnalyticsResponse.CategoryState.builder().amount(amountT).categoryId(categoryId).categoryName(name).build());
        }

        // fifth field
        List<TransactionInformationPart> transactionInformationPartList = transactionEntityList.stream().map(
                x->TransactionInformationPart.builder().transactionId(x.getTransactionId()).name(x.getName()).description(x.getDescription())
                        .amount(x.getAmount()).currency(accountEntity.getCurrency()).categoryType(x.getCategoryType())
                        .createdAt(x.getCreatedAt()).categories(mapTransactionIdsToListOfCategoryNames.get(x.getTransactionId())).build()
        ).toList();



        // products, 6th field
        List<AnalyticsResponse.Products> productsList = new ArrayList<>();
        List<TransactionLineItemsEntity> transactionLineItemsEntities = transactionLineItemsRepository.fetchAllTransactionsLineProductsWithIn(transactionIds);

        // set of unique product names
        Set<String> uniqueNamesOfProducts = transactionLineItemsEntities.stream().map(TransactionLineItemsEntity::getProductName).collect(Collectors.toSet());
        for(var prodName: uniqueNamesOfProducts){
                BigDecimal sumOfCost = transactionLineItemsEntities.stream().filter(x->x.getProductName().equals(prodName))
                        .map(TransactionLineItemsEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                int numberOfPurchases =(int)transactionLineItemsEntities.stream().filter(x->x.getProductName().equals(prodName)).count();
                productsList.add(AnalyticsResponse.Products.builder().productName(prodName).amount(sumOfCost).numberOfPurchases(numberOfPurchases).build());
        }

        // field 10 11 12
        Map<String, BigDecimal> mapDateAmountIncome = income.stream().collect(Collectors.toMap(AnalyticsResponse.CategoryKindAmount::getXAxisData,
                AnalyticsResponse.CategoryKindAmount::getAmount,
                BigDecimal::add,
                LinkedHashMap::new));
        Map<String, BigDecimal> mapDateAmountExpense = expense.stream().collect(Collectors.toMap(AnalyticsResponse.CategoryKindAmount::getXAxisData,
                AnalyticsResponse.CategoryKindAmount::getAmount,
                BigDecimal::add,
                LinkedHashMap::new));

        Map<String, BigDecimal> mapDateAmountNet = income.stream().collect(Collectors.toMap(AnalyticsResponse.CategoryKindAmount::getXAxisData, x->x.getAmount().subtract(mapDateAmountExpense.get(x.getXAxisData())),   BigDecimal::add,
                LinkedHashMap::new ));



        //  last two fields
         Map<String, List<AnalyticsResponse.CategoryState>> stringListMapCategoriesByDate = mapDatesToCategoryStates(categoryEntitiesSet,transactionCategoriesEntityMap,buckets);
        Map<String, List<AnalyticsResponse.Products>> stringListMapProductByDate =mapDatesToProductStates(transactionLineItemsEntities, buckets);




        return AnalyticsResponse.builder().accountId(accountId)
                .accountName(accountEntity.getName())
                .state(accountEntity.getCurrentBalance())
                .currency(accountEntity.getCurrency())
                .incomeTotal(incomeTotal)
                .expenseTotal(expenseTotal)
                .net(net)
                .income(income)
                .expense(expense)
                .categoryStateListExpense(categoryStateListExpense)
                .categoryStateListIncome(categoryStateListIncome)
                .transactionInformationPartList(transactionInformationPartList)
                .productsList(productsList)
                .mapDateAmountIncome(mapDateAmountIncome)
                .mapDateAmountExpense(mapDateAmountExpense)
                .mapDateAmountNet(mapDateAmountNet)
                .stringListMapCategoriesByDate(stringListMapCategoriesByDate)
                .stringListMapProductByDate(stringListMapProductByDate)
                .build();
    }

    @Override
    public DashboardAnalyticsResponse getDashboardAnalytics( UUID userId) {

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("No user found with id={}", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });

        // get all user accounts
        List<AccountEntity> accountEntityList = accountRepository.getAllAccounts(userId);
        if(accountEntityList.isEmpty()){
            return DashboardAnalyticsResponse.builder()
                    .totalBalanceMonthlyList(List.of())
                    .monthlyExpenses(List.of())
                    .monthlyIncomes(List.of())
                    .accountBalances(List.of())
                    .latestTransactions(List.of())
                    .categoryAmountsIncome(List.of())
                    .categoryAmountsExpense(List.of())
                    .build();
        }


        List<List<TotalBalanceMonthly>> totalBalanceMonthliesPerAcc = new ArrayList<>();
        List<List<MonthlyExpenses>> monthlyExpensesPerAcc = new ArrayList<>();
        List<List<MonthlyIncome>> monthlyIncomesPerAcc = new ArrayList<>();
        List<AccountBalance> accountBalances = new ArrayList<>();

        Map<String, BigDecimal> balanceByMonth = new LinkedHashMap<>();
        Map<String, BigDecimal> incomeByMonth  = new LinkedHashMap<>();
        Map<String, BigDecimal> expenseByMonth = new LinkedHashMap<>();

        // inside the for loop we first decide current date then date - 3 years meaning with the current one 4 years worth of data
        // then we extract the income related and expense related transactions and group them by months
        // then we pad the missing dates with 0s
        // balance is calculated => balance = (balance + 1 month) - (income + 1 month) + (expense + 1 month)
        for(AccountEntity account : accountEntityList){
            Year currentYear = Year.now(ZoneOffset.UTC);

            OffsetDateTime accountCreated = account.getCreatedAt();
            OffsetDateTime windowStart = OffsetDateTime.of(currentYear.getValue() - 3, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime timeNow = OffsetDateTime.now(ZoneOffset.UTC);

            OffsetDateTime accountStart = accountCreated.minusMonths(1);
            YearMonth start = YearMonth.from(accountStart.isAfter(windowStart) ? accountStart : windowStart);
            YearMonth end = YearMonth.from(timeNow);

            OffsetDateTime fromDate = start.atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC);

            List<MonthlyBalance> transactionEntityListIncome = transactionRepository.findMonthlyAmountsByMonth(account.getAccountId(),
                   fromDate, List.of(CategoryKind.INCOME,CategoryKind.ADJUSTMENT_PLUS, CategoryKind.TRANSFER_FROM));
            transactionEntityListIncome.forEach(
                    x->x.setCurrency(account.getCurrency())
            );

            List<MonthlyBalance> transactionEntityListExpense = transactionRepository.findMonthlyAmountsByMonth(account.getAccountId(),
                    fromDate, List.of(CategoryKind.EXPENSE,CategoryKind.ADJUSTMENT_MINUS, CategoryKind.TRANSFER_TO));
            transactionEntityListExpense.forEach(
                    x->x.setCurrency(account.getCurrency())
            );


            List<MonthlyIncome> transactionEntityListIncomeCleaned = fillMissingMonthsIncome(transactionEntityListIncome, start, end);
            List<MonthlyExpenses> transactionEntityListExpenseCleaned = fillMissingMonthsExpense(transactionEntityListExpense, start, end);

            BigDecimal currentAccountBalance = account.getCurrentBalance();

            List<TotalBalanceMonthly> totalBalanceByMonth = fillTotalBalanceBasedOnCurrentState(currentAccountBalance,
                    transactionEntityListIncomeCleaned,
                    transactionEntityListExpenseCleaned,
                    start,
                    end);

            totalBalanceMonthliesPerAcc.add(totalBalanceByMonth);
            monthlyExpensesPerAcc.add(transactionEntityListExpenseCleaned);
            monthlyIncomesPerAcc.add(transactionEntityListIncomeCleaned);

            accountBalances.add(AccountBalance.builder()
                    .totalBalance(account.getCurrentBalance())
                    .currency(account.getCurrency())
                    .accountName(account.getName())
                    .bankNumLast4(account.getAccountNumberMasked())
                    .id(account.getAccountId())
                    .institution(account.getInstitutionName())
                    .build());

            totalBalanceByMonth.forEach(x ->
                    balanceByMonth.merge(x.getMonth(), x.getTotalBalance(), BigDecimal::add));
            transactionEntityListIncomeCleaned.forEach(x ->
                    incomeByMonth.merge(x.getMonth(), x.getTotalIncome(), BigDecimal::add));
            transactionEntityListExpenseCleaned.forEach(x ->
                    expenseByMonth.merge(x.getMonth(), x.getTotalExpenses(), BigDecimal::add));

        }


        List<TotalBalanceMonthly> profileBalance = balanceByMonth.entrySet().stream()
                .map(e -> TotalBalanceMonthly.builder()
                        .currency("EUR")
                        .month(e.getKey())
                        .totalBalance(e.getValue())
                        .build())
                .toList();

        List<MonthlyIncome> profileIncome = incomeByMonth.entrySet().stream()
                .map(e -> MonthlyIncome.builder()
                        .currency("EUR")
                        .month(e.getKey())
                        .totalIncome(e.getValue())
                        .build())
                .toList();

        List<MonthlyExpenses> profileExpenses = expenseByMonth.entrySet().stream()
                .map(e -> MonthlyExpenses.builder()
                        .currency("EUR")
                        .month(e.getKey())
                        .totalExpenses(e.getValue())
                        .build())
                .toList();


        // Now find latest 10 transactions
        List<TransactionEntity> transactionEntityList = transactionRepository.findRecentTransactions(userId, PageRequest.of(0, 10));
        List<UUID> transactionIdsForTop10 = transactionEntityList.stream().map(TransactionEntity::getTransactionId).toList();
        List<TransactionCategoriesEntity> transactionCategoriesEntitiesTop10 = transactionCategoriesRepository.fetchAllTransactionCategoryIdsInWithJoin(transactionIdsForTop10);
        Map<UUID, Set<String>> mapUUIDCategoryNames = transactionCategoriesEntitiesTop10.stream()
                .collect(Collectors.groupingBy(x->x.getId().getTransactionId(), Collectors.mapping(x->x.getCategory().getName(), Collectors.toSet())));

        List<LatestTransactions> latestTransactions = transactionEntityList.stream()
                .map(x-> LatestTransactions.builder()
                        .amount(x.getAmount())
                        .currency(x.getCurrency())
                        .transactionName(x.getName())
                        .dateTimeZone(x.getCreatedAt())
                        .categories(mapUUIDCategoryNames.get(x.getTransactionId()).stream().toList())
                        .categoryKind(x.getCategoryType())
                        .accountName(x.getAccount().getName())
                        .transactionId(x.getTransactionId())
                        .build()).toList();


        // find this months category amounts
        OffsetDateTime timeTo = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime timeFrom = timeTo.withDayOfMonth(1)
                .truncatedTo(ChronoUnit.DAYS);

        List<TransactionEntity> transactionEntityListThisMonth = transactionRepository.findThisMonthsTransactions(userId, timeFrom, timeTo);
        List<UUID> transactionIdsForMonth = transactionEntityListThisMonth.stream().map(TransactionEntity::getTransactionId).toList();
        List<TransactionCategoriesEntity> transactionCategoriesEntitiesMonth = transactionCategoriesRepository.fetchAllTransactionCategoryIdsInWithJoin(transactionIdsForTop10);

        Map<String, Set<TransactionEntity>> stringSetMap = transactionCategoriesEntitiesMonth.stream()
                .collect(Collectors.groupingBy(x->x.getCategory().getName(), Collectors.mapping(x->x.getTransaction(), Collectors.toSet())));

        List<CategoryAmounts> categoryAmountsIncome = new ArrayList<>();
        List<CategoryAmounts> categoryAmountsExpense = new ArrayList<>();
        for(String catName: stringSetMap.keySet()){
            BigDecimal sumAmount = stringSetMap.get(catName).stream().map(x->x.getAmount())
                    .reduce(BigDecimal.ZERO, (a, b)->a.add(b));

            CategoryKind categoryKind = stringSetMap.get(catName).stream()
                    .findFirst()
                    .map(TransactionEntity::getCategoryType)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            if (categoryKind == CategoryKind.EXPENSE ||
                    categoryKind == CategoryKind.ADJUSTMENT_MINUS ||
                    categoryKind == CategoryKind.TRANSFER_TO) {

                categoryAmountsExpense.add(CategoryAmounts.builder().categoryName(catName).categoryKind(categoryKind).amount(sumAmount).build());

            } else {
                categoryAmountsIncome.add(CategoryAmounts.builder().categoryName(catName).categoryKind(categoryKind).amount(sumAmount).build());
            }

        }




        return DashboardAnalyticsResponse.builder()
                .totalBalanceMonthlyList(profileBalance)
                .monthlyExpenses(profileExpenses)
                .monthlyIncomes(profileIncome)
                .accountBalances(accountBalances)
                .latestTransactions(latestTransactions)
                .categoryAmountsIncome(categoryAmountsIncome)
                .categoryAmountsExpense(categoryAmountsExpense)
                .build();
    }


}
