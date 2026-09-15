package com.monetrax.monetrax.transactions.service.impl;

import com.monetrax.monetrax.accounts.dto.AccountInformation;
import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.accounts.exceptions.NoSuchAccountFound;
import com.monetrax.monetrax.accounts.mapper.AccountMapper;
import com.monetrax.monetrax.accounts.repository.AccountRepository;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.categories.mapper.CategoryMapper;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.transactions.dto.*;
import com.monetrax.monetrax.transactions.entity.*;
import com.monetrax.monetrax.transactions.exceptions.IllegalStateDeletionException;
import com.monetrax.monetrax.transactions.exceptions.InvalidTransactionCreationException;
import com.monetrax.monetrax.transactions.exceptions.MissingTransactionLikeEntityException;
import com.monetrax.monetrax.transactions.exceptions.MissingTransactionUpdatedFieldsException;
import com.monetrax.monetrax.transactions.mapper.GlobalTransactionMapper;
import com.monetrax.monetrax.transactions.repository.TransactionAdditionalInfoRepository;
import com.monetrax.monetrax.transactions.repository.TransactionCategoriesRepository;
import com.monetrax.monetrax.transactions.repository.TransactionLineItemsRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.transactions.service.TransactionService;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    public TransactionServiceImpl(TransactionAdditionalInfoRepository transactionAdditionalInfoRepository, TransactionLineItemsRepository transactionLineItemsRepository, TransactionCategoriesRepository transactionCategoriesRepository, TransactionRepository transactionRepository, GlobalTransactionMapper globalTransactionMapper, CategoryRepository categoryRepository, CategoryMapper categoryMapper, AccountMapper accountMapper, AccountRepository accountRepository, UserRepository userRepository) {
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
        if(selectedCategories.isEmpty())
            throw new InvalidTransactionCreationException("You need to select one or more categories!");

        // get all categories available to user
        List<CategoryEntity> listOfAllAvailableCategories = categoryRepository.fetchUsersAndDefaultCategories(true, userId);
        Map<UUID, CategoryEntity> categoryEntityMap = listOfAllAvailableCategories.stream()
                .collect(Collectors.toMap(CategoryEntity::getCategoryId, e->e));


        // selected categories should be turned to set
        Set<RequestedCategoryInformation> requestedCategoryInformationSet = new HashSet<>(selectedCategories);

        // Here we are checking if categories are correct
        // get one category which we will compare to the rest of selected ones and if one is different we will throw exception
        final CategoryKind categoryKind = categoryEntityMap.get(selectedCategories.get(0).getCategoryId()).getCategoryType();
        requestedCategoryInformationSet.forEach((e)->{
            CategoryEntity categoryItem = categoryEntityMap.get(e.getCategoryId());
            if(categoryItem == null)
                throw new InvalidTransactionCreationException("One or more selected categories are invalid.");
            if(categoryKind!=categoryItem.getCategoryType())
                throw new InvalidTransactionCreationException("All selected categories must be of the same type.");
        });

        return CategoryRelatedData.builder()
                .categoryEntityMap(categoryEntityMap)
                .categoryKind(categoryKind)
                .listOfAllAvailableCategories(listOfAllAvailableCategories)
                .requestedCategoryInformationSet(requestedCategoryInformationSet)
                .build();
    }


    @Override
    public TransactionInformation getTransactionInformation(UUID transactionId, UUID userId) {

        // Fetching data here
        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(()-> new MissingTransactionLikeEntityException("No such transaction found !"));
        List<TransactionAdditionalInfoEntity>  transactionAdditionalInfoEntities = transactionAdditionalInfoRepository.fetchAllTransactionsAdditionalInfo(transactionId);
        List<TransactionLineItemsEntity> transactionLineItemsEntities =  transactionLineItemsRepository.fetchAllTransactionsLineProducts(transactionId);
        List<TransactionCategoriesEntity> transactionCategoriesEntities = transactionCategoriesRepository.fetchAllTransactionCategoryIds(transactionId);
        List<UUID> categoryIds = transactionCategoriesEntities.stream()
                .map(tc -> tc.getId().getCategoryId())
                .toList();
        List<CategoryEntity> categories = categoryRepository.findAllById(categoryIds);

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
        AccountEntity account = accountRepository.findAccountNonLock(userId, accountId).orElseThrow(()->{
            return new NoSuchAccountFound("No such account exists!");
        });

        List<TransactionEntity> transactionEntities = transactionRepository.fetchAllAccountTransactions(accountId);
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
        return ListOfAccountTransactions.builder()
                .account(accountInformation)
                .transactions(informationParts)
                .build();


    }

    @Override
    @Transactional
    public TransactionCreateUpdateResponse createTransaction(TransactionCreate transactionCreate, UUID userId, UUID accountId) {

        // fetch user and account
        UserEntity user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));
        AccountEntity account = accountRepository.getAccount(userId, accountId).orElseThrow(()->{
            return new NoSuchAccountFound("No such account exists!");
        });

        // method for checking the validity of categories, getting category map categoryUUID - categoryEntity, Set/deduplication of the the categories
        // and shared category kind between all of the categories
        CategoryRelatedData categoryRelatedData = checkCreateUpdateCategoryValidity(transactionCreate.getCategories(), userId);
        Map<UUID, CategoryEntity> categoryEntityMap = categoryRelatedData.getCategoryEntityMap();
        Set<RequestedCategoryInformation> requestedCategoryInformationSet = categoryRelatedData.getRequestedCategoryInformationSet();
        CategoryKind categoryKind = categoryRelatedData.getCategoryKind();

        //************************************************************************************************************************
        // conversion logic should be implemented bellow
        //************************************************************************************************************************
        BigDecimal amountNative = transactionCreate.getAmount();
        //************************************************************************************************************************

        // save transaction and get uuid
        TransactionEntity transactionEntity = globalTransactionMapper.fromTransactionCreateToTransactionEntity(transactionCreate,
                user,
                account,
                amountNative,
                categoryKind,
                new BigDecimal("1.00"));

        TransactionEntity transactionEntitySaved  = transactionRepository.save(transactionEntity);


        // save TransactionCategoryEntity
        List<TransactionCategoriesEntity> transactionCategoriesEntities = new ArrayList<>();
        for(var x: requestedCategoryInformationSet){
            var tce = new TransactionCategoriesEmbeddable(transactionEntitySaved.getTransactionId(), x.getCategoryId());
            transactionCategoriesEntities.add(new TransactionCategoriesEntity(tce, transactionEntitySaved, categoryEntityMap.get(x.getCategoryId())));
        }
        transactionCategoriesRepository.saveAll(transactionCategoriesEntities);

        // save TransactionAdditionalInfo
        if(!transactionCreate.getAdditionalInfo().isEmpty()){
            List<TransactionAdditionalInfoEntity> transactionAdditionalInfoEntities = new ArrayList<>();
            for(var x: transactionCreate.getAdditionalInfo()){
                transactionAdditionalInfoEntities.add(TransactionAdditionalInfoEntity.builder()
                        .amount(x.getAmount())
                        .kind(x.getKind())
                        .label(x.getLabel())
                        .transaction(transactionEntitySaved)
                        .build());
            }
            transactionAdditionalInfoRepository.saveAll(transactionAdditionalInfoEntities);
        }

        // save TransactionLineProducts
        if(!transactionCreate.getLineInformation().isEmpty()){
            List<TransactionLineItemsEntity> transactionLineItemsEntities = new ArrayList<>();
            for(var x: transactionCreate.getLineInformation()){
                transactionLineItemsEntities.add(TransactionLineItemsEntity.builder()
                        .amount(x.getAmount())
                        .productName(x.getProductName())
                        .transaction(transactionEntitySaved)
                        .build());
            }
            transactionLineItemsRepository.saveAll(transactionLineItemsEntities);
        }

        // finally update the account amount
        BigDecimal updatedCurrentBalance = switch (categoryKind) {
            case INCOME, ADJUSTMENT_PLUS, TRANSFER_FROM -> account.getCurrentBalance().add(transactionEntitySaved.getAmountNative());
            case EXPENSE, ADJUSTMENT_MINUS, TRANSFER_TO -> account.getCurrentBalance().subtract(transactionEntitySaved.getAmountNative());
        };

        account.setCurrentBalance(updatedCurrentBalance);
        accountRepository.save(account);

        //*************************************************************************************************************************
        // code for handling alerts
        //*************************************************************************************************************************

        return new TransactionCreateUpdateResponse("Transaction successfully created.", transactionEntitySaved.getTransactionId());
    }

    @Override
    @Transactional
    public TransactionCreateUpdateResponse updateTransaction(TransactionUpdate transactionUpdate, UUID userId, UUID transactionId) {

        if (transactionUpdate.getName() == null
                && transactionUpdate.getDescription() == null
                && transactionUpdate.getAmount() == null
                && transactionUpdate.getConversionFactor() == null
                && (transactionUpdate.getCategories() == null
                || transactionUpdate.getCategories().isEmpty())) {

            throw new MissingTransactionUpdatedFieldsException("At least one field needs to be provided.");
        }

        //fetch transaction
        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(()-> new MissingTransactionLikeEntityException("No such transaction found !"));

        //fetch user and account
        UserEntity user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));
        AccountEntity account = accountRepository.getAccount(userId, transactionEntity.getAccount().getAccountId()).orElseThrow(()->{
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
            List<TransactionCategoriesEntity> transactionCategoriesEntityList = transactionCategoriesRepository.fetchAllTransactionCategoryIds(transactionId);
            int num_of_deleted = transactionCategoriesRepository.deleteAllTransactionCategoriesByTransactionId(transactionId);
            if (num_of_deleted != transactionCategoriesEntityList.size()) {
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
        }

        //*************************************************************************************************************************
        // code for handling alerts
        //*************************************************************************************************************************

        return new TransactionCreateUpdateResponse("Transaction successfully updated.", transactionEntity.getTransactionId());
    }

    @Override
    @Transactional
    public TransactionCreateUpdateResponse deleteTransaction(UUID userId, UUID transactionId) {
        //fetch transaction
        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(()-> new MissingTransactionLikeEntityException("No such transaction found !"));

        //fetch user and account
        UserEntity user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));
        AccountEntity account = accountRepository.getAccount(userId, transactionEntity.getAccount().getAccountId()).orElseThrow(()->{
            return new NoSuchAccountFound("No such account exists!");
        });

        // Delete all transaction categories
        List<TransactionCategoriesEntity> transactionCategoriesEntityList = transactionCategoriesRepository.fetchAllTransactionCategoryIds(transactionId);
        int num_of_deleted = transactionCategoriesRepository.deleteAllTransactionCategoriesByTransactionId(transactionId);
        if (num_of_deleted != transactionCategoriesEntityList.size()) {
            throw new IllegalStateDeletionException("Category deletion count mismatch for transaction " + transactionId);
        }

        // Delete all additional info categories
        List<TransactionAdditionalInfoEntity> transactionAdditionalInfoEntities = transactionAdditionalInfoRepository.fetchAllTransactionsAdditionalInfo(transactionId);
        if(!transactionAdditionalInfoEntities.isEmpty()){
            int num_of_deletion = transactionAdditionalInfoRepository.deleteAllTransactionAdditionalInfoByTransactionId(transactionId);
            if (num_of_deletion != transactionAdditionalInfoEntities.size()) {
                throw new IllegalStateDeletionException("Additional information deletion count mismatch for transaction " + transactionId);
            }
        }

        // Delete all line items categories
        List<TransactionLineItemsEntity> transactionLineItemsEntities = transactionLineItemsRepository.fetchAllTransactionsLineProducts(transactionId);
        if(!transactionLineItemsEntities.isEmpty()){
            int num_of_deletion = transactionLineItemsRepository.deleteAllTransactionLineItemsByTransactionId(transactionId);
            if (num_of_deletion != transactionLineItemsEntities.size()) {
                throw new IllegalStateDeletionException("Line items deletion count mismatch for transaction " + transactionId);
            }
        }

        BigDecimal revertCurrentBalance = switch (transactionEntity.getCategoryType()) {
            case INCOME, ADJUSTMENT_PLUS, TRANSFER_FROM -> account.getCurrentBalance().subtract(transactionEntity.getAmountNative());
            case EXPENSE, ADJUSTMENT_MINUS, TRANSFER_TO -> account.getCurrentBalance().add(transactionEntity.getAmountNative());
        };

        account.setCurrentBalance(revertCurrentBalance);
        accountRepository.save(account);

        transactionRepository.delete(transactionEntity);

        return new TransactionCreateUpdateResponse("Transaction successfully deleted.", transactionEntity.getTransactionId());
    }


}
