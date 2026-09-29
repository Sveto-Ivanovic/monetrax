package com.monetrax.monetrax.categories.service.impl;

import com.monetrax.monetrax.categories.dto.*;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.exceptions.CategoryAlreadyExistsException;
import com.monetrax.monetrax.categories.exceptions.ForbiddenCategoryDeletionException;
import com.monetrax.monetrax.categories.exceptions.MissingFieldsForCategoryUpdate;
import com.monetrax.monetrax.categories.exceptions.NoSuchCategoryExistsException;
import com.monetrax.monetrax.categories.mapper.CategoryMapper;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.categories.service.CategoryService;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.repository.TransactionCategoriesRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRecurrenceRuleRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionCategoriesRepository transactionCategoriesRepository;
    private final TransactionRecurrenceRuleRepository transactionRecurrenceRuleRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, UserRepository userRepository, TransactionRepository transactionRepository, TransactionCategoriesRepository transactionCategoriesRepository, TransactionRecurrenceRuleRepository transactionRecurrenceRuleRepository){
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.transactionCategoriesRepository = transactionCategoriesRepository;
        this.transactionRecurrenceRuleRepository = transactionRecurrenceRuleRepository;
    }

    public CategoryInformation getCategory(UUID categoryId, UUID userId){
        log.debug("Fetching category [categoryId={}, userId={}]", categoryId, userId);
        CategoryEntity resp = categoryRepository.fetchUsersCategory(categoryId, userId).orElseThrow(() -> {
            log.warn("Category not found [categoryId={}, userId={}]", categoryId, userId);
            return new NoSuchCategoryExistsException("No category with id: " + categoryId);
        });
        log.debug("Fetched category [categoryId={}, userId={}]", categoryId, userId);
        return categoryMapper.fromCategoryEntityToCategoryInformation(resp);
    }

    public CategoryInformation createCategory(CategoryCreate category, UUID userId){
        log.info("Creating category [userId={}]", userId);

        if(categoryRepository.fetchUsersAndDefaultCategoriesNames(userId).contains(category.getName())){
            log.warn("Category creation rejected, name already exists [userId={}, name={}]", userId, category.getName());
            throw new CategoryAlreadyExistsException("The category name already exists.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Category creation failed, user not found [userId={}]", userId);
            return new NoSuchUserExistsException("No user with id: " + userId);
        });
        CategoryEntity categoryToCreate = categoryMapper.fromCategoryCreateToCategoryEntity(category, user);
        CategoryEntity savedCategory = categoryRepository.save(categoryToCreate);
        log.info("Category created [categoryId={}, userId={}]", savedCategory.getCategoryId(), userId);
        return categoryMapper.fromCategoryEntityToCategoryInformation(savedCategory);
    }

    public CategoryDeletionSuccess deleteCategory(UUID categoryId, UUID userId){
        log.info("Deleting category [categoryId={}, userId={}]", categoryId, userId);

        CategoryEntity resp = categoryRepository.fetchUsersCategory(categoryId, userId).orElseThrow(() -> {
            log.warn("Category deletion failed, category not found [categoryId={}, userId={}]", categoryId, userId);
            return new NoSuchCategoryExistsException("No category with id: " + categoryId);
        });
        List<TransactionEntity> transactionEntityList = transactionRepository.fetchAllUserTransactions(userId);
        List<UUID> transactionUUIDs = transactionEntityList.stream()
                .map(TransactionEntity::getTransactionId)
                .toList();
        log.debug("Checking category usage [categoryId={}, userId={}, transactionCount={}]", categoryId, userId, transactionUUIDs.size());

        int countOfOccurrencesOfCategoryInsideTransactions = transactionCategoriesRepository.countCategoriesInUserTransactions(transactionUUIDs, categoryId);
        if(countOfOccurrencesOfCategoryInsideTransactions!=0){
            log.warn("Category deletion blocked, used in transactions [categoryId={}, userId={}, count={}]", categoryId, userId, countOfOccurrencesOfCategoryInsideTransactions);
            throw new ForbiddenCategoryDeletionException("Category is used in %d transaction/s. Please remove them before deleting the category."
                    .formatted(countOfOccurrencesOfCategoryInsideTransactions));
        }

        int countOfTransactionRecurrenceRules = transactionRecurrenceRuleRepository.countTransactionRecurrenceRulesThatUseCategoryBasedOnTransactions(transactionUUIDs);
        if(countOfTransactionRecurrenceRules!=0){
            log.warn("Category deletion blocked, used in recurrence rules [categoryId={}, userId={}, count={}]", categoryId, userId, countOfTransactionRecurrenceRules);
            throw new ForbiddenCategoryDeletionException("Category is used in %d transaction rule recurrence/s. Please remove them before deleting the category."
                    .formatted(countOfTransactionRecurrenceRules));
        }

        categoryRepository.delete(resp);
        log.info("Category deleted [categoryId={}, userId={}]", categoryId, userId);
        return new CategoryDeletionSuccess("Successfully deleted category.", true);
    }

    public CategoryInformation updateCategory(CategoryUpdate categoryToUpdate, UUID categoryId, UUID userId){
        log.info("Updating category [categoryId={}, userId={}]", categoryId, userId);

        if (categoryToUpdate.getDescription() == null && categoryToUpdate.getName() == null) {
            log.warn("Category update rejected, no fields provided [categoryId={}, userId={}]", categoryId, userId);
            throw new MissingFieldsForCategoryUpdate("At least one field must be provided for update");
        }

        if(categoryRepository.fetchUsersAndDefaultCategoriesNames(userId).contains(categoryToUpdate.getName())){
            log.warn("Category update rejected, name already exists [categoryId={}, userId={}, name={}]", categoryId, userId, categoryToUpdate.getName());
            throw new CategoryAlreadyExistsException("The category name already exists.");
        }

        CategoryEntity resp = categoryRepository.fetchUsersCategory(categoryId, userId).orElseThrow(() -> {
            log.warn("Category update failed, category not found [categoryId={}, userId={}]", categoryId, userId);
            return new NoSuchCategoryExistsException("No category with id: " + categoryId);
        });

        Optional.ofNullable(categoryToUpdate.getDescription()).ifPresent(resp::setDescription);
        Optional.ofNullable(categoryToUpdate.getName()).ifPresent(resp::setName);

        resp.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        CategoryEntity savedCategory = categoryRepository.save(resp);
        log.info("Category updated [categoryId={}, userId={}]", categoryId, userId);

        return categoryMapper.fromCategoryEntityToCategoryInformation(savedCategory);
    }

    public FetchAllCategoriesResponse getAllCategories(UUID userId){
        log.debug("Fetching all categories [userId={}]", userId);
        List<CategoryEntity> listOfUserCategories = categoryRepository.fetchUsersAndDefaultCategories(true, userId);
        log.debug("Fetched all categories [userId={}, count={}]", userId, listOfUserCategories.size());
        return new FetchAllCategoriesResponse(categoryMapper.fromListOfCategoryEntityToListOfCategoryInformation(listOfUserCategories));
    }
}