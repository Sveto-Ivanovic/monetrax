package com.monetrax.monetrax.transactions.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.accounts.exceptions.NoSuchAccountFound;
import com.monetrax.monetrax.accounts.mapper.AccountMapper;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.ai.entity.ApiKeysEntity;
import com.monetrax.monetrax.ai.entity.KeyType;
import com.monetrax.monetrax.ai.entity.MessageHistoryEntity;
import com.monetrax.monetrax.ai.entity.MessageStatus;
import com.monetrax.monetrax.ai.exceptions.AiProviderException;
import com.monetrax.monetrax.ai.exceptions.NoSuchApiKeyExistsException;
import com.monetrax.monetrax.ai.repository.ApiKeysRepository;
import com.monetrax.monetrax.ai.repository.MessageHistoryRepository;
import com.monetrax.monetrax.ai.service.AiAPiService;
import com.monetrax.monetrax.ai.service.impl.AiAPiServiceImpl;
import com.monetrax.monetrax.alerts.exceptions.InvalidInputException;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.categories.mapper.CategoryMapper;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.transactions.component.AiComponent;
import com.monetrax.monetrax.transactions.dto.*;
import com.monetrax.monetrax.transactions.entity.*;
import com.monetrax.monetrax.transactions.exceptions.IllegalStateDeletionException;
import com.monetrax.monetrax.transactions.exceptions.InvalidTransactionCreationException;
import com.monetrax.monetrax.transactions.exceptions.MissingTransactionLikeEntityException;
import com.monetrax.monetrax.transactions.exceptions.MissingTransactionUpdatedFieldsException;
import com.monetrax.monetrax.transactions.mapper.GlobalTransactionMapper;
import com.monetrax.monetrax.transactions.repository.*;
import com.monetrax.monetrax.transactions.service.TransactionService;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionAdditionalInfoRepository transactionAdditionalInfoRepository;
    private final TransactionLineItemsRepository transactionLineItemsRepository;
    private final TransactionCategoriesRepository transactionCategoriesRepository;
    private final TransactionRepository transactionRepository;
    private final GlobalTransactionMapper globalTransactionMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private  final UserRepository userRepository;
    private final TransactionRecurrenceRuleRepository transactionRecurrenceRuleRepository;
    private final ApiKeysRepository apiKeysRepository;
    private final MessageHistoryRepository messageHistoryRepository;
    private final AiAPiServiceImpl aiAPiService;
    private final AiComponent aiComponent;
    private final Validator validator;

    public TransactionServiceImpl(TransactionAdditionalInfoRepository transactionAdditionalInfoRepository, TransactionLineItemsRepository transactionLineItemsRepository, TransactionCategoriesRepository transactionCategoriesRepository, TransactionRepository transactionRepository, GlobalTransactionMapper globalTransactionMapper, CategoryRepository categoryRepository, CategoryMapper categoryMapper, AccountMapper accountMapper, AccountRepository accountRepository, UserRepository userRepository, TransactionRecurrenceRuleRepository transactionRecurrenceRuleRepository, ApiKeysRepository apiKeysRepository, MessageHistoryRepository messageHistoryRepository, AiAPiServiceImpl aiAPiService, AiComponent aiComponent, Validator validator) {
        this.transactionAdditionalInfoRepository = transactionAdditionalInfoRepository;
        this.transactionLineItemsRepository = transactionLineItemsRepository;
        this.transactionCategoriesRepository = transactionCategoriesRepository;
        this.transactionRepository = transactionRepository;
        this.globalTransactionMapper = globalTransactionMapper;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRecurrenceRuleRepository = transactionRecurrenceRuleRepository;

        // these last four were added for the AI transaction endpoint
        this.apiKeysRepository = apiKeysRepository;
        this.messageHistoryRepository = messageHistoryRepository;
        this.aiAPiService = aiAPiService;
        this.aiComponent = aiComponent;
        this.validator = validator;
    }

    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @Data
    static class CategoryRelatedData {
        List<CategoryEntity> listOfAllAvailableCategories;
        Map<UUID, CategoryEntity> categoryEntityMap;
        Set<RequestedCategoryInformation> requestedCategoryInformationSet;
        CategoryKind categoryKind;
    }

    private CategoryRelatedData checkCreateUpdateCategoryValidity( List<RequestedCategoryInformation> selectedCategories, UUID userId){
        //check if categories don't exist or if all categories don't share the same category type
        if(selectedCategories.isEmpty()) {
            log.warn("Category validation failed, no categories selected [userId={}]", userId);
            throw new InvalidTransactionCreationException("You need to select one or more categories!");
        }

        // get all categories available to user
        List<CategoryEntity> listOfAllAvailableCategories = categoryRepository.fetchUsersAndDefaultCategories(true, userId);
        Map<UUID, CategoryEntity> categoryEntityMap = listOfAllAvailableCategories.stream()
                .collect(Collectors.toMap(CategoryEntity::getCategoryId, e->e));


        // selected categories should be turned to set
        Set<RequestedCategoryInformation> requestedCategoryInformationSet = new HashSet<>(selectedCategories);

        // Here we are checking if categories are correct
        // get one category which we will compare to the rest of selected ones and if one is different we will throw exception
        CategoryEntity categoryEntity = categoryEntityMap.get(selectedCategories.get(0).getCategoryId());
        if(categoryEntity == null) {
            log.warn("Category validation failed, invalid category selected [userId={}, categoryId={}]", userId, selectedCategories.get(0).getCategoryId());
            throw new InvalidTransactionCreationException("One or more selected categories are invalid.");
        }
        final CategoryKind categoryKind = categoryEntity.getCategoryType();

        requestedCategoryInformationSet.forEach((e)->{
            CategoryEntity categoryItem = categoryEntityMap.get(e.getCategoryId());
            if(categoryItem == null) {
                log.warn("Category validation failed, invalid category selected [userId={}, categoryId={}]", userId, e.getCategoryId());
                throw new InvalidTransactionCreationException("One or more selected categories are invalid.");
            }
            if(categoryKind!=categoryItem.getCategoryType()) {
                log.warn("Category validation failed, mixed category types [userId={}, expectedKind={}, categoryId={}, foundKind={}]", userId, categoryKind, e.getCategoryId(), categoryItem.getCategoryType());
                throw new InvalidTransactionCreationException("All selected categories must be of the same type.");
            }
        });

        log.debug("Category validation passed [userId={}, count={}, categoryKind={}]", userId, requestedCategoryInformationSet.size(), categoryKind);
        return CategoryRelatedData.builder()
                .categoryEntityMap(categoryEntityMap)
                .categoryKind(categoryKind)
                .listOfAllAvailableCategories(listOfAllAvailableCategories)
                .requestedCategoryInformationSet(requestedCategoryInformationSet)
                .build();
    }


    @Override
    public TransactionInformation getTransactionInformation(UUID transactionId, UUID userId) {
        log.debug("Fetching transaction information [transactionId={}, userId={}]", transactionId, userId);

        // Fetching data here
        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(()-> {
            log.warn("Transaction fetch failed, transaction not found [transactionId={}, userId={}]", transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction found !");
        });
        List<TransactionAdditionalInfoEntity>  transactionAdditionalInfoEntities = transactionAdditionalInfoRepository.fetchAllTransactionsAdditionalInfo(transactionId);
        List<TransactionLineItemsEntity> transactionLineItemsEntities =  transactionLineItemsRepository.fetchAllTransactionsLineProducts(transactionId);
        List<TransactionCategoriesEntity> transactionCategoriesEntities = transactionCategoriesRepository.fetchAllTransactionCategoryIds(transactionId);
        List<UUID> categoryIds = transactionCategoriesEntities.stream()
                .map(tc -> tc.getId().getCategoryId())
                .toList();
        List<CategoryEntity> categories = categoryRepository.findAllById(categoryIds);
        log.debug("Fetched transaction data [transactionId={}, additionalInfoCount={}, lineItemCount={}, categoryCount={}]", transactionId, transactionAdditionalInfoEntities.size(), transactionLineItemsEntities.size(), categories.size());

        // Preprocessing data here
        List<TransactionAdditionalInfoInformation> additionalInfoInformations = transactionAdditionalInfoEntities.stream()
                .map(globalTransactionMapper::fromTransactionAdditionalInfoEntityToTransactionAdditionalInfoInformation)
                .toList();
        List<TransactionLineItemsInformation> lineItemsInformations = transactionLineItemsEntities.stream()
                .map(globalTransactionMapper::fromTransactionLineItemsEntityToTransactionLineItemsInformation)
                .toList();

        List<CategoryInformation> categoryInformations = categories.stream()
                .map(categoryMapper::fromCategoryEntityToCategoryInformation)
                .toList();

        return globalTransactionMapper.fromTransactionEntityToTransactionInformation(transactionEntity, additionalInfoInformations, lineItemsInformations, categoryInformations);

    }

    @Override
    public ListOfAccountTransactions getAccountTransactions(UUID accountId, UUID userId) {
        log.debug("Fetching account transactions [accountId={}, userId={}]", accountId, userId);

        AccountEntity account = accountRepository.findAccountNonLock(userId, accountId).orElseThrow(()->{
            log.warn("Account transactions fetch failed, account not found [accountId={}, userId={}]", accountId, userId);
            return new NoSuchAccountFound("No such account exists!");
        });

        List<TransactionEntity> transactionEntities = transactionRepository.fetchAllAccountTransactions(accountId);
        log.debug("Fetched account transactions [accountId={}, transactionCount={}]", accountId, transactionEntities.size());
        List<TransactionInformationPart> informationParts;

        // if we have transactions in the account
        if(!transactionEntities.isEmpty()){
            // get all transaction ids
            List<UUID> allTransactionIds = transactionEntities.stream()
                    .map(TransactionEntity::getTransactionId).toList();

            // use those transaction ids to fetch all corresponding rows from the table transaction_categories, which contains relation between transaction and its catefories
            List<TransactionCategoriesEntity> transactionCategoriesEntities = transactionCategoriesRepository.fetchAllTransactionCategoryIdsIn(allTransactionIds);
            // making map for faster processing, we now have map of transaction ids - list of category ids
            Map<UUID, Set<UUID>> transactionCategoryMap = transactionCategoriesEntities.stream()
                    .collect(Collectors.groupingBy(
                            tc -> tc.getId().getTransactionId(),
                            Collectors.mapping(tc -> tc.getId().getCategoryId(), Collectors.toSet())
                    ));

            // fetch all category ids from all transactions, put them into set, as transactions can have multiple categories and there is bound to be multiple copies of the same uuids
            Set<UUID> categoryIds = transactionCategoriesEntities.stream()
                    .map(e->e.getId().getCategoryId())
                    .collect(Collectors.toSet());

            // fetch the categories based on unique category ids
            List<CategoryEntity> categories = categoryRepository.findAllById(categoryIds);
            Map<UUID, CategoryEntity> uuidSetCategoryEntityMap = categories.stream()
                    .collect(Collectors.toMap(CategoryEntity::getCategoryId, x->x));
            log.debug("Fetched categories for account transactions [accountId={}, uniqueCategoryCount={}]", accountId, categories.size());


            // Finally map of transaction id - list of category Entities
            Map<UUID, List<CategoryEntity>> uuidTransactionKeyEntityVal = transactionCategoryMap.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, y->{
                        Set<UUID> uuids = y.getValue();
                        return uuids.stream()
                                .map(uuidSetCategoryEntityMap::get)
                                .toList();
                    }));


            // constructing TransactionInformationPart custom class via stream
            informationParts = transactionEntities.stream()
                    .map(t->{

                        List<CategoryEntity> subListOfCategoriesTiedToTransaction = uuidTransactionKeyEntityVal.get(t.getTransactionId());
                        // use mapper to create the TransactionInformationPart
                        return globalTransactionMapper.fromTransactionEntityToTransactionInformationPart(t, subListOfCategoriesTiedToTransaction);
                    }).toList();
        }
        else{
            informationParts=new ArrayList<>();
        }

        //*************************************************************************************************************************
        // logic for fetching alerts
        //*************************************************************************************************************************

        AccountInformation accountInformation = accountMapper.fromAccountEntityToAccountInformation(account);
        log.debug("Built account transactions response [accountId={}, userId={}, count={}]", accountId, userId, informationParts.size());
        return ListOfAccountTransactions.builder()
                .account(accountInformation)
                .transactions(informationParts)
                .build();


    }

    @Override
    @Transactional
    public TransactionCreateUpdateResponse createTransaction(TransactionCreate transactionCreate, UUID userId, UUID accountId) {
        log.info("Creating transaction for userId={}, accountId={}", userId, accountId);

        // fetch user and account
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("No user found with id={}", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(() -> {
            log.warn("No account found for userId={}, accountId={}", userId, accountId);
            return new NoSuchAccountFound("No such account exists!");
        });
        log.debug("Fetched user and account successfully: userId={}, accountId={}", userId, accountId);

        // method for checking the validity of categories, getting category map categoryUUID - categoryEntity, Set/deduplication of the the categories
        // and shared category kind between all of the categories
        CategoryRelatedData categoryRelatedData = checkCreateUpdateCategoryValidity(transactionCreate.getCategories(), userId);
        Map<UUID, CategoryEntity> categoryEntityMap = categoryRelatedData.getCategoryEntityMap();
        Set<RequestedCategoryInformation> requestedCategoryInformationSet = categoryRelatedData.getRequestedCategoryInformationSet();
        CategoryKind categoryKind = categoryRelatedData.getCategoryKind();
        log.debug("Validated categories: count={}, categoryKind={}", requestedCategoryInformationSet.size(), categoryKind);
        //************************************************************************************************************************
        // conversion logic should be implemented bellow
        //************************************************************************************************************************
        BigDecimal amountNative = transactionCreate.getAmount();
        //************************************************************************************************************************

        if(!categoryKind.equals(CategoryKind.EXPENSE) && !transactionCreate.getLineInformation().isEmpty()){
            throw new InvalidInputException("The additional information fields are only allowed for Expense category kinds.");
        }


        // save transaction and get uuid
        TransactionEntity transactionEntity = globalTransactionMapper.fromTransactionCreateToTransactionEntity(transactionCreate,
                user,
                account,
                amountNative,
                categoryKind,
                new BigDecimal("1.00"));

        TransactionEntity transactionEntitySaved = transactionRepository.save(transactionEntity);
        log.info("Saved transaction with id={} for accountId={}", transactionEntitySaved.getTransactionId(), accountId);

        // save TransactionCategoryEntity
        List<TransactionCategoriesEntity> transactionCategoriesEntities = new ArrayList<>();
        for (var x : requestedCategoryInformationSet) {
            var tce = new TransactionCategoriesEmbeddable(transactionEntitySaved.getTransactionId(), x.getCategoryId());
            transactionCategoriesEntities.add(new TransactionCategoriesEntity(tce, transactionEntitySaved, categoryEntityMap.get(x.getCategoryId())));
        }
        transactionCategoriesRepository.saveAll(transactionCategoriesEntities);
        log.debug("Saved {} transaction-category links for transactionId={}", transactionCategoriesEntities.size(), transactionEntitySaved.getTransactionId());

        // save TransactionAdditionalInfo
        if (!transactionCreate.getAdditionalInfo().isEmpty()) {
            List<TransactionAdditionalInfoEntity> transactionAdditionalInfoEntities = new ArrayList<>();
            for (var x : transactionCreate.getAdditionalInfo()) {
                transactionAdditionalInfoEntities.add(TransactionAdditionalInfoEntity.builder()
                        .amount(x.getAmount())
                        .kind(x.getKind())
                        .label(x.getLabel())
                        .transaction(transactionEntitySaved)
                        .build());
            }
            transactionAdditionalInfoRepository.saveAll(transactionAdditionalInfoEntities);
            log.debug("Saved {} additional info entries for transactionId={}", transactionAdditionalInfoEntities.size(), transactionEntitySaved.getTransactionId());
        } else {
            log.debug("No additional info to save for transactionId={}", transactionEntitySaved.getTransactionId());
        }

        // save TransactionLineProducts
        if (!transactionCreate.getLineInformation().isEmpty()) {
            List<TransactionLineItemsEntity> transactionLineItemsEntities = new ArrayList<>();
            for (var x : transactionCreate.getLineInformation()) {
                transactionLineItemsEntities.add(TransactionLineItemsEntity.builder()
                        .amount(x.getAmount())
                        .productName(x.getProductName())
                        .transaction(transactionEntitySaved)
                        .build());
            }
            transactionLineItemsRepository.saveAll(transactionLineItemsEntities);
            log.debug("Saved {} line items for transactionId={}", transactionLineItemsEntities.size(), transactionEntitySaved.getTransactionId());
        } else {
            log.debug("No line items to save for transactionId={}", transactionEntitySaved.getTransactionId());
        }

        // finally update the account amount
        BigDecimal previousBalance = account.getCurrentBalance();
        BigDecimal updatedCurrentBalance = switch (categoryKind) {
            case INCOME, ADJUSTMENT_PLUS, TRANSFER_FROM -> account.getCurrentBalance().add(transactionEntitySaved.getAmountNative());
            case EXPENSE, ADJUSTMENT_MINUS, TRANSFER_TO -> account.getCurrentBalance().subtract(transactionEntitySaved.getAmountNative());
        };

        account.setCurrentBalance(updatedCurrentBalance);
        account.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        accountRepository.save(account);
        log.info("Updated account balance for accountId={}, categoryKind={}", accountId, categoryKind);

        if (transactionCreate.getTransactionRecurrenceRule() != null) {

            LocalDate nextRun = switch (transactionCreate.getTransactionRecurrenceRule().getRuleType()) {
                case DAY -> LocalDate.now(ZoneOffset.UTC).plusDays(transactionCreate.getTransactionRecurrenceRule().getRecurrenceNum());
                case WEEK -> LocalDate.now(ZoneOffset.UTC).plusWeeks(transactionCreate.getTransactionRecurrenceRule().getRecurrenceNum());
                case MONTH -> LocalDate.now(ZoneOffset.UTC).plusMonths(transactionCreate.getTransactionRecurrenceRule().getRecurrenceNum());
                case YEAR -> LocalDate.now(ZoneOffset.UTC).plusYears(transactionCreate.getTransactionRecurrenceRule().getRecurrenceNum());
            };

            var transactionRule = TransactionRecurrenceRuleEntity.builder()
                    .sourceTransaction(transactionEntitySaved)
                    .intervalCount(transactionCreate.getTransactionRecurrenceRule().getRecurrenceNum())
                    .lastRunDate(LocalDate.now(ZoneOffset.UTC))
                    .maxOccurrences(transactionCreate.getTransactionRecurrenceRule().getMaxNumOfOccurrencesAllowed())
                    .nextRunDate(nextRun)
                    .occurrencesGenerated(0)
                    .recurrenceUnit(transactionCreate.getTransactionRecurrenceRule().getRuleType())
                    .build();

            transactionRecurrenceRuleRepository.save(transactionRule);
            log.info("Created recurrence rule for transactionId={}, nextRunDate={}", transactionEntitySaved.getTransactionId(), nextRun);
        } else {
            log.debug("No recurrence rule specified for transactionId={}", transactionEntitySaved.getTransactionId());
        }

        log.info("Transaction creation completed successfully for transactionId={}", transactionEntitySaved.getTransactionId());
        return new TransactionCreateUpdateResponse("Transaction successfully created.", transactionEntitySaved.getTransactionId());
    }

    @Override
    @Transactional
    public TransactionCreateUpdateResponse updateTransaction(TransactionUpdate transactionUpdate, UUID userId, UUID transactionId) {
        log.info("Updating transaction [transactionId={}, userId={}]", transactionId, userId);

        if (transactionUpdate.getName() == null
                && transactionUpdate.getDescription() == null
                && transactionUpdate.getAmount() == null
                && transactionUpdate.getConversionFactor() == null
                && (transactionUpdate.getCategories() == null
                || transactionUpdate.getCategories().isEmpty())) {

            log.warn("Transaction update rejected, no fields provided [transactionId={}, userId={}]", transactionId, userId);
            throw new MissingTransactionUpdatedFieldsException("At least one field needs to be provided.");
        }

        //fetch transaction
        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(()-> {
            log.warn("Transaction update failed, transaction not found [transactionId={}, userId={}]", transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction found !");
        });

        //fetch user and account
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> {
            log.warn("Transaction update failed, user not found [userId={}]", userId);
            return new NoSuchUserExistsException("No user with id: "+ userId);
        });
        AccountEntity account = accountRepository.getAccount(userId, transactionEntity.getAccount().getAccountId()).orElseThrow(()->{
            log.warn("Transaction update failed, account not found [transactionId={}, userId={}]", transactionId, userId);
            return new NoSuchAccountFound("No such account exists!");
        });

        // original amounts
        BigDecimal nativeAmountPrevious = transactionEntity.getAmountNative();
        CategoryKind previousCategory = transactionEntity.getCategoryType();

        // logic tied to categories
        if((transactionUpdate.getCategories() != null
                && !transactionUpdate.getCategories().isEmpty())){
            // method for checking the validity of categories, getting category map categoryUUID - categoryEntity, Set/deduplication of the the categories
            // and shared category kind between all of the categories
            CategoryRelatedData categoryRelatedData = checkCreateUpdateCategoryValidity(transactionUpdate.getCategories(), userId);
            Map<UUID, CategoryEntity> categoryEntityMap = categoryRelatedData.getCategoryEntityMap();
            Set<RequestedCategoryInformation> requestedCategoryInformationSet = categoryRelatedData.getRequestedCategoryInformationSet();
            CategoryKind categoryKind = categoryRelatedData.getCategoryKind();

            // update the TransactionCategory table
            // First delete all the transactions
            int countOfCategories = transactionCategoriesRepository.countTransactionCategories(transactionId);
            int num_of_deleted = transactionCategoriesRepository.deleteAllTransactionCategoriesByTransactionId(transactionId);
            if (num_of_deleted != countOfCategories) {
                log.error("Category deletion count mismatch during update [transactionId={}, expected={}, deleted={}]", transactionId, countOfCategories, num_of_deleted);
                throw new IllegalStateDeletionException("Category deletion count mismatch for transaction " + transactionId);
            }

            // update with new ones
            List<TransactionCategoriesEntity> transactionCategoriesEntities = new ArrayList<>();
            for(var x: requestedCategoryInformationSet){
                var tce = new TransactionCategoriesEmbeddable(transactionEntity.getTransactionId(), x.getCategoryId());
                transactionCategoriesEntities.add(new TransactionCategoriesEntity(tce, transactionEntity, categoryEntityMap.get(x.getCategoryId())));
            }
            transactionCategoriesRepository.saveAll(transactionCategoriesEntities);
            Optional.ofNullable(categoryKind).ifPresent(transactionEntity::setCategoryType);
            log.debug("Updated transaction categories [transactionId={}, count={}, previousKind={}, newKind={}]", transactionId, transactionCategoriesEntities.size(), previousCategory, categoryKind);
        }

        // here we don't need conversion logic either we use the old or the user provided
        BigDecimal amountNative = null;
        if(transactionUpdate.getAmount()!=null){
            if(transactionUpdate.getConversionFactor()==null)
                amountNative = transactionUpdate.getAmount().multiply(transactionEntity.getConversionFactor());
            else
                amountNative = transactionUpdate.getAmount().multiply(transactionUpdate.getConversionFactor());
        }

        // update and save transaction
        Optional.ofNullable(transactionUpdate.getName()).ifPresent(transactionEntity::setName);
        Optional.ofNullable(transactionUpdate.getConversionFactor()).ifPresent(transactionEntity::setConversionFactor);
        Optional.ofNullable(transactionUpdate.getAmount()).ifPresent(transactionEntity::setAmount);
        Optional.ofNullable(transactionUpdate.getDescription()).ifPresent(transactionEntity::setDescription);
        Optional.ofNullable(amountNative).ifPresent(transactionEntity::setAmountNative);


        TransactionEntity transactionEntityUpdated = transactionRepository.save(transactionEntity);
        log.debug("Saved updated transaction [transactionId={}]", transactionId);


        if(amountNative!=null || transactionEntityUpdated.getCategoryType() != previousCategory){
            // revert old vals
            BigDecimal revertCurrentBalance = switch (previousCategory) {
                case INCOME, ADJUSTMENT_PLUS, TRANSFER_FROM -> account.getCurrentBalance().subtract(nativeAmountPrevious);
                case EXPENSE, ADJUSTMENT_MINUS, TRANSFER_TO -> account.getCurrentBalance().add(nativeAmountPrevious);
            };
            BigDecimal updatedCurrentBalance;
            if(amountNative!=null){
                // update account with new vals
                updatedCurrentBalance = switch (transactionEntityUpdated.getCategoryType()) {
                    case INCOME, ADJUSTMENT_PLUS, TRANSFER_FROM -> revertCurrentBalance.add(amountNative);
                    case EXPENSE, ADJUSTMENT_MINUS, TRANSFER_TO -> revertCurrentBalance.subtract(amountNative);
                };
            } else{
                // case when we only switch categories, in case expense -> income we increment by + 2xtransaction amount from old state
                updatedCurrentBalance = switch (transactionEntityUpdated.getCategoryType()) {
                    case INCOME, ADJUSTMENT_PLUS, TRANSFER_FROM -> revertCurrentBalance.add(nativeAmountPrevious);
                    case EXPENSE, ADJUSTMENT_MINUS, TRANSFER_TO -> revertCurrentBalance.subtract(nativeAmountPrevious);
                };
            }


            account.setCurrentBalance(updatedCurrentBalance);
            accountRepository.save(account);
            log.info("Recalculated account balance after transaction update [accountId={}, transactionId={}, previousKind={}, newKind={}]", account.getAccountId(), transactionId, previousCategory, transactionEntityUpdated.getCategoryType());
        }

        //*************************************************************************************************************************
        // code for handling alerts
        //*************************************************************************************************************************

        log.info("Transaction updated successfully [transactionId={}, userId={}]", transactionId, userId);
        return new TransactionCreateUpdateResponse("Transaction successfully updated.", transactionEntity.getTransactionId());
    }

    @Override
    @Transactional
    public TransactionCreateUpdateResponse deleteTransaction(UUID userId, UUID transactionId) {
        log.info("Deleting transaction [transactionId={}, userId={}]", transactionId, userId);

        //fetch transaction
        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(()-> {
            log.warn("Transaction deletion failed, transaction not found [transactionId={}, userId={}]", transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction found !");
        });

        //fetch user and account
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> {
            log.warn("Transaction deletion failed, user not found [userId={}]", userId);
            return new NoSuchUserExistsException("No user with id: "+ userId);
        });
        AccountEntity account = accountRepository.getAccount(userId, transactionEntity.getAccount().getAccountId()).orElseThrow(()->{
            log.warn("Transaction deletion failed, account not found [transactionId={}, userId={}]", transactionId, userId);
            return new NoSuchAccountFound("No such account exists!");
        });

        // Delete all transaction categories
        List<TransactionCategoriesEntity> transactionCategoriesEntityList = transactionCategoriesRepository.fetchAllTransactionCategoryIds(transactionId);
        int num_of_deleted = transactionCategoriesRepository.deleteAllTransactionCategoriesByTransactionId(transactionId);
        if (num_of_deleted != transactionCategoriesEntityList.size()) {
            log.error("Category deletion count mismatch during transaction deletion [transactionId={}, expected={}, deleted={}]", transactionId, transactionCategoriesEntityList.size(), num_of_deleted);
            throw new IllegalStateDeletionException("Category deletion count mismatch for transaction " + transactionId);
        }
        log.debug("Deleted {} transaction-category links [transactionId={}]", num_of_deleted, transactionId);

        // Delete all additional info categories
        List<TransactionAdditionalInfoEntity> transactionAdditionalInfoEntities = transactionAdditionalInfoRepository.fetchAllTransactionsAdditionalInfo(transactionId);
        if(!transactionAdditionalInfoEntities.isEmpty()){
            int num_of_deletion = transactionAdditionalInfoRepository.deleteAllTransactionAdditionalInfoByTransactionId(transactionId);
            if (num_of_deletion != transactionAdditionalInfoEntities.size()) {
                log.error("Additional info deletion count mismatch during transaction deletion [transactionId={}, expected={}, deleted={}]", transactionId, transactionAdditionalInfoEntities.size(), num_of_deletion);
                throw new IllegalStateDeletionException("Additional information deletion count mismatch for transaction " + transactionId);
            }
            log.debug("Deleted {} additional info entries [transactionId={}]", num_of_deletion, transactionId);
        }

        // Delete all line items categories
        List<TransactionLineItemsEntity> transactionLineItemsEntities = transactionLineItemsRepository.fetchAllTransactionsLineProducts(transactionId);
        if(!transactionLineItemsEntities.isEmpty()){
            int num_of_deletion = transactionLineItemsRepository.deleteAllTransactionLineItemsByTransactionId(transactionId);
            if (num_of_deletion != transactionLineItemsEntities.size()) {
                log.error("Line items deletion count mismatch during transaction deletion [transactionId={}, expected={}, deleted={}]", transactionId, transactionLineItemsEntities.size(), num_of_deletion);
                throw new IllegalStateDeletionException("Line items deletion count mismatch for transaction " + transactionId);
            }
            log.debug("Deleted {} line items [transactionId={}]", num_of_deletion, transactionId);
        }

        // delete transaction recurrences
        Optional<TransactionRecurrenceRuleEntity> transactionRecurrenceRuleEntity = transactionRecurrenceRuleRepository.fetchRuleWithTransactionId(transactionId);
        transactionRecurrenceRuleEntity.ifPresent(transactionRecurrenceRuleRepository::delete);
        log.debug("Recurrence rule deletion [transactionId={}, ruleFound={}]", transactionId, transactionRecurrenceRuleEntity.isPresent());

        BigDecimal revertCurrentBalance = switch (transactionEntity.getCategoryType()) {
            case INCOME, ADJUSTMENT_PLUS, TRANSFER_FROM -> account.getCurrentBalance().subtract(transactionEntity.getAmountNative());
            case EXPENSE, ADJUSTMENT_MINUS, TRANSFER_TO -> account.getCurrentBalance().add(transactionEntity.getAmountNative());
        };

        account.setCurrentBalance(revertCurrentBalance);
        accountRepository.save(account);
        log.info("Reverted account balance after transaction deletion [accountId={}, transactionId={}]", account.getAccountId(), transactionId);

        transactionRepository.delete(transactionEntity);

        log.info("Transaction deleted successfully [transactionId={}, userId={}]", transactionId, userId);
        return new TransactionCreateUpdateResponse("Transaction successfully deleted.", transactionEntity.getTransactionId());
    }
    // create transaction via AI
    @Override
    @Transactional
    public TransactionCreateUpdateResponse createTransactionViaAi(TransactionCreateAIRequest transactionCreate, UUID userId, UUID accountId) {

        log.info("AI transaction creation started for userId={}, accountId={}, model={}, messageLength={}",
                userId, accountId, transactionCreate.getGeminiModel(),
                transactionCreate.getMsg() != null ? transactionCreate.getMsg().length() : 0);

        ObjectMapper objectMapper = new ObjectMapper();

        // get account and user
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(() -> {
            log.warn("AI transaction creation failed, no account found [userId={}, accountId={}]", userId, accountId);
            return new NoSuchAccountFound("No such account exists!");
        });
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("AI transaction creation failed, user not found [userId={}]", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });
        log.debug("Fetched user and account for AI transaction: userId={}, accountId={}, accountCurrency={}",
                userId, accountId, account.getCurrency());

        // extract transaction from user String
        TransactionExtraction transactionExtraction;
        if (transactionCreate.getGeminiModel() != null) {

            // first fetch api key for gemini
            ApiKeysEntity apiKeysEntity = apiKeysRepository.fetchApiKey(KeyType.GEMINI_API_KEY, userId).orElseThrow(() -> {
                log.warn("AI transaction creation failed, no api key found [userId={}, keyType={}]", userId, KeyType.GEMINI_API_KEY);
                return new NoSuchApiKeyExistsException("Gemini api key has not been inserted!");
            });
            log.debug("Fetched api key entity for userId={}, keyType={}", userId, KeyType.GEMINI_API_KEY);

            // decrypt the key and call gemini ai (never log the key itself)
            String geminiApiKey = aiAPiService.localDecrypt(apiKeysEntity.getApiKeyEncrypted(), userId, KeyType.GEMINI_API_KEY);
            long startNanos = System.nanoTime();
            try {
                log.info("Calling Gemini for userId={}, model={}", userId, transactionCreate.getGeminiModel());
                transactionExtraction = aiComponent.extractGemini(transactionCreate.getMsg(), account.getCurrency(), transactionCreate.getGeminiModel(), geminiApiKey);
                log.info("Gemini call succeeded for userId={}, durationMs={}", userId, (System.nanoTime() - startNanos) / 1_000_000);
            } catch (Exception e) {
                // only the exception type is logged: the message/stack trace may contain request details
                log.warn("Gemini call failed for userId={}, model={}, durationMs={}, errorType={}",
                        userId, transactionCreate.getGeminiModel(), (System.nanoTime() - startNanos) / 1_000_000, e.getClass().getSimpleName());
                throw new AiProviderException("AI request failed. Check your API key and quota.");
            }

            // this part here is just saving the message as history
            String transactionExtractionString;
            try {
                transactionExtractionString = objectMapper.writeValueAsString(transactionExtraction);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize extraction result for userId={}", userId, e);
                throw new RuntimeException(e);
            }

            // if error is present
            if (transactionExtraction.getError() != null && !transactionExtraction.getError().contains("null")&& !transactionExtraction.getError().isBlank()) {
                log.info("AI extraction returned an error for userId={}: {}", userId, transactionExtraction.getError());

                MessageHistoryEntity messageHistoryEntity = MessageHistoryEntity.builder()
                        .errorMessage(transactionExtraction.getError())
                        .rawMessage(transactionCreate.getMsg())
                        .apiKey(apiKeysEntity)
                        .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .inputTokens(0)
                        .outputTokens(0)
                        .model(String.valueOf(transactionCreate.getGeminiModel()))
                        .provider("Google")
                        .status(MessageStatus.FAILED)
                        .structuredOutput(transactionExtractionString)
                        .user(user)
                        .updatedAt(OffsetDateTime.now())
                        .build();
                messageHistoryRepository.save(messageHistoryEntity);
                log.debug("Saved FAILED message history for userId={}", userId);

                return TransactionCreateUpdateResponse.builder().msg(transactionExtraction.getError()).build();
            } else {
                log.info("AI extraction succeeded for userId={}: lineItems={}, currency={}",
                        userId,
                        transactionExtraction.getLineInformation() != null ? transactionExtraction.getLineInformation().size() : 0,
                        transactionExtraction.getCurrency());

                MessageHistoryEntity messageHistoryEntity = MessageHistoryEntity.builder()
                        .errorMessage(null)
                        .rawMessage(transactionCreate.getMsg())
                        .apiKey(apiKeysEntity)
                        .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .inputTokens(0)
                        .outputTokens(0)
                        .model(String.valueOf(transactionCreate.getGeminiModel()))
                        .provider("Google")
                        .status(MessageStatus.SUCCESS)
                        .structuredOutput(transactionExtractionString)
                        .user(user)
                        .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .build();
                messageHistoryRepository.save(messageHistoryEntity);
                log.debug("Saved SUCCESS message history for userId={}", userId);

                // we don't have an error and everything is extracted
                // construct create transaction object so we can just pass it as input to the already finished method
                TransactionCreate transactionCreate1 = TransactionCreate.builder()
                        .additionalInfo(List.of())
                        .amount(transactionExtraction.getAmount().setScale(2, RoundingMode.HALF_UP))
                        .categories(transactionCreate.getCategories())
                        .currency(transactionExtraction.getCurrency())
                        .description(transactionExtraction.getDescription())
                        .lineInformation(transactionExtraction.getLineInformation() != null
                                ? transactionExtraction.getLineInformation() : new ArrayList<>())
                        .name(transactionExtraction.getName())
                        .transactionRecurrenceRule(transactionCreate.getTransactionRecurrenceRule())
                        .customCreationDate(transactionCreate.getCustomCreationDate())
                        .build();
                log.debug("Built TransactionCreate from AI extraction for userId={}", userId);

                // validate if everything is correct; if not, the message history is rolled back along with the transaction, which is acceptable
                Set<ConstraintViolation<TransactionCreate>> violations = validator.validate(transactionCreate1);

                if (!violations.isEmpty()) {
                    log.warn("AI extraction failed validation for userId={}, violationCount={}, violations={}",
                            userId,
                            violations.size(),
                            violations.stream()
                                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                                    .toList());
                    throw new ConstraintViolationException(violations);
                }
                log.debug("AI extraction passed validation for userId={}", userId);

                TransactionCreateUpdateResponse response = createTransaction(transactionCreate1, userId, accountId);
                log.info("AI transaction creation completed for userId={}, accountId={}, transactionId={}",
                        userId, accountId, response.getTransactionId());
                return response;
            }
        } else {
            log.warn("AI transaction creation rejected, no supported model provided [userId={}]", userId);
            throw new InvalidInputException("Currently only gemini ai transaction creation is supported");
        }
    }



}