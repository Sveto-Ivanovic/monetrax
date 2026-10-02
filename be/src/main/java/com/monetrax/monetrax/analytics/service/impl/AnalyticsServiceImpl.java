package com.monetrax.monetrax.analytics.service.impl;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.alerts.exceptions.InvalidInputException;
import com.monetrax.monetrax.analytics.dto.AnalyticsRequest;
import com.monetrax.monetrax.analytics.dto.AnalyticsResponse;
import com.monetrax.monetrax.analytics.dto.GroupByTypes;
import com.monetrax.monetrax.analytics.service.AnalyticsService;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
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


    private List<String> buildBuckets(LocalDate from, LocalDate to, GroupByTypes groupBy, DateTimeFormatter fmt) {
        List<String> buckets = new ArrayList<>();

        if(groupBy == GroupByTypes.DAY){
            for(LocalDate fromDay = from; !fromDay.isAfter(to); fromDay = fromDay.plusDays(1))
                buckets.add(fromDay.format(fmt));
        } else if (groupBy == GroupByTypes.MONTH) {
            YearMonth toMonth = YearMonth.from(to);
            for(YearMonth fromMonth = YearMonth.from(from); !fromMonth.isAfter(toMonth);fromMonth= fromMonth.plusMonths(1))
                buckets.add(fromMonth.format(fmt));
        }else{
            Year toYears = Year.from(to);
            for(Year fromYears = Year.from(from); !fromYears.isAfter(toYears);fromYears= fromYears.plusYears(1))
                buckets.add(fromYears.format(fmt));
        }

        if(buckets.size()>500){
            throw new InvalidInputException("To many buckets present, consider shortening the date range or pick bigger group by parameter.");
        }

        return buckets;
    }


    private List<AnalyticsResponse.CategoryKindAmount> aggregateByKind(
            List<TransactionEntity> transactionEntityList,
            CategoryKind categoryKind,
            DateTimeFormatter formatter,
            List<String> buckets

    ){

        Map<String, BigDecimal> sums =  transactionEntityList.stream()
                .filter(x->x.getCategoryType().equals(categoryKind))
                .collect(
                        Collectors.groupingBy(
                                x -> x.getCreatedAt().toLocalDate().format(formatter),
                                Collectors.mapping(TransactionEntity::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                        )
                );



                return buckets.stream()
                        .map(x->{

                            return  AnalyticsResponse.CategoryKindAmount.builder()
                                    .amount(sums.getOrDefault(x, BigDecimal.ZERO)).categoryKind(categoryKind)
                                    .xAxisData(x)
                                    .build();
                        }).toList();

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
        List<AccountEntity> accountEntityList;
        if(analyticsRequest.getAccountIds()==null || analyticsRequest.getAccountIds().isEmpty()) {
            accountEntityList = accountRepository.getAllAccounts(userId);
        }
        else{
            accountEntityList = accountRepository.getAllAccountsByAccountIds(userId, analyticsRequest.getAccountIds());
        }

        if(accountEntityList.isEmpty()){
            throw  new IllegalStateException("No accounts found for analytics");
        }

        // extract the variables of interest
        OffsetDateTime dateFrom = analyticsRequest.getDateFrom().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime dateTo = analyticsRequest.getDateTo().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        GroupByTypes groupByType = analyticsRequest.getGroupBy();

        DateTimeFormatter formatter = switch (groupByType) {
            case DAY -> DateTimeFormatter.ofPattern("yyyy-MM-dd");
            case MONTH -> DateTimeFormatter.ofPattern("yyyy-MM");
            default -> DateTimeFormatter.ofPattern("yyyy");
        };

        List<String> buckets = buildBuckets(analyticsRequest.getDateFrom(), analyticsRequest.getDateTo(), groupByType, formatter);

        List<AnalyticsResponse.AccountTransactionsByCategoryKindAndCategoryAggregated> perAccount = new ArrayList<>();
        for(var account : accountEntityList){

            UUID accountId = account.getAccountId();
            List<TransactionEntity> transactionEntityList = transactionRepository.fetchUserTransactionsInsideSpecifiedDate(userId, accountId, dateFrom, dateTo);
            if(transactionEntityList.isEmpty())
                continue;
            // all transaction ids used
            List<UUID> transactionIds = transactionEntityList.stream().map(TransactionEntity::getTransactionId).toList();


            List<TransactionCategoriesEntity> transactionCategoriesEntityList = transactionCategoriesRepository.fetchAllTransactionCategoryIdsInWithJoin(transactionIds);
            // map category ids to list of transactionCategories
            Map<UUID, List<TransactionCategoriesEntity>> transactionCategoriesEntityMap = transactionCategoriesEntityList.stream()
                    .collect(Collectors.groupingBy( x->x.getId().getCategoryId(), Collectors.mapping(x->x, Collectors.toList()) ));



            // Unique Categories in used
            Set<CategoryEntity> categoryEntitiesSet = transactionCategoriesEntityList.stream().map(TransactionCategoriesEntity::getCategory).collect(Collectors.toSet());


            // the category type vals
            List<AnalyticsResponse.CategoryKindAmount> income = aggregateByKind(transactionEntityList,CategoryKind.INCOME, formatter, buckets);
            List<AnalyticsResponse.CategoryKindAmount> expense = aggregateByKind(transactionEntityList,CategoryKind.EXPENSE, formatter, buckets);
            List<AnalyticsResponse.CategoryKindAmount> transferFrom =aggregateByKind(transactionEntityList,CategoryKind.TRANSFER_FROM, formatter, buckets);
            List<AnalyticsResponse.CategoryKindAmount> transferTo = aggregateByKind(transactionEntityList,CategoryKind.TRANSFER_TO, formatter, buckets);
            List<AnalyticsResponse.CategoryKindAmount> adjustmentPlus = aggregateByKind(transactionEntityList,CategoryKind.ADJUSTMENT_PLUS, formatter, buckets);
            List<AnalyticsResponse.CategoryKindAmount> adjustmentMinus = aggregateByKind(transactionEntityList,CategoryKind.ADJUSTMENT_MINUS, formatter, buckets);

            // we go through unique categories used here
             List<AnalyticsResponse.CategoryAmount> categoryKindAmmountListList = new ArrayList<>();
             List<UUID> uniqueCategoryIds = new ArrayList<>();
             List<String> uniqueCategoryNames = new ArrayList<>();

            for(var category : categoryEntitiesSet){
                UUID categoryId = category.getCategoryId();
                String name = category.getName();

                // get all transactions that use this category
                List<TransactionCategoriesEntity> transactionCategoriesEntityList1 = transactionCategoriesEntityMap.get(categoryId);

                // map of dates and sums
                Map<String, BigDecimal> sum = transactionCategoriesEntityList1.stream()
                                .collect(Collectors.groupingBy(
                                        x -> x.getTransaction().getCreatedAt().toLocalDate().format(formatter),
                                        Collectors.mapping(x->x.getTransaction().getAmount(), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                                        ));

                // add buckets and sort
                Map<String, BigDecimal> mapOfSums = buckets.stream()
                        .collect(Collectors.toMap(x->x, x-> sum.get(x) == null? BigDecimal.ZERO : sum.get(x), (a,b)->b, LinkedHashMap::new));

                categoryKindAmmountListList.add(AnalyticsResponse.CategoryAmount.builder().categoryName(name)
                        .categoryId(categoryId)
                        .amount(mapOfSums.values().stream().toList())
                        .xAxisData(mapOfSums.keySet().stream().toList()).build());

                uniqueCategoryIds.add(categoryId);
                uniqueCategoryNames.add(name);
            }



            // finally products
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



            // finally we insert these inputs into list of account specific metrics
            perAccount.add(AnalyticsResponse.AccountTransactionsByCategoryKindAndCategoryAggregated.builder()
                    .accountId(accountId)
                    .accountName(account.getName())
                    .state(account.getCurrentBalance())
                    .income(income)
                    .expense(expense)
                    .transferFrom(transferFrom)
                    .transferTo(transferTo)
                    .adjustmentPlus(adjustmentPlus)
                    .adjustmentMinus(adjustmentMinus)
                    .categoryKindAmmountListList(categoryKindAmmountListList)
                    .uniqueCategoryIds(uniqueCategoryIds)
                    .uniqueCategoryNames(uniqueCategoryNames)
                    .productsList(productsList)
                    .build());

        }


        return AnalyticsResponse.builder().listOfTransactionsByCategoryKindAggregated(perAccount).build();
    }



}
