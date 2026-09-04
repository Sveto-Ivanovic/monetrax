package com.monetrax.monetrax.categories.service.impl;

import com.monetrax.monetrax.categories.dto.*;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.exceptions.MissingFieldsForCategoryUpdate;
import com.monetrax.monetrax.categories.exceptions.NoSuchCategoryExistsException;
import com.monetrax.monetrax.categories.mapper.CategoryMapper;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.categories.service.CategoryService;
import com.monetrax.monetrax.user.entity.UserEntity;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;
import com.monetrax.monetrax.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;
    private UserRepository userRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, UserRepository userRepository){
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.userRepository = userRepository;
    }

    public CategoryInformation getCategory(UUID categoryId, UUID userId){
        CategoryEntity resp =categoryRepository.fetchUsersCategory(categoryId, userId).orElseThrow(()->new NoSuchCategoryExistsException("No category with id: "+ categoryId));
        return categoryMapper.fromCategoryEntityToCategoryInformation(resp);
    }

    public CategoryInformation createCategory(CategoryCreate category, UUID userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("No user with id: "+ userId));
        CategoryEntity categoryToCreate = categoryMapper.fromCategoryCreateToCategoryEntity(category, user);
        CategoryEntity savedCategory = categoryRepository.save(categoryToCreate);
        return categoryMapper.fromCategoryEntityToCategoryInformation(savedCategory);
    }

    public CategoryDeletionSuccess deleteCategory(UUID categoryId, UUID userId){
        CategoryEntity resp =categoryRepository.fetchUsersCategory(categoryId, userId).orElseThrow(()->new NoSuchCategoryExistsException("No category with id: "+ categoryId));

        categoryRepository.delete(resp);
        return new CategoryDeletionSuccess("Successfully deleted category.", true);
    }

    public CategoryInformation updateCategory(CategoryUpdate categoryToUpdate, UUID categoryId, UUID userId){
        if (categoryToUpdate.getCategoryType() == null && categoryToUpdate.getDescription() == null && categoryToUpdate.getName() == null) {
            throw new MissingFieldsForCategoryUpdate("At least one field must be provided for update");
        }

        CategoryEntity resp =categoryRepository.fetchUsersCategory(categoryId, userId).orElseThrow(()->new NoSuchCategoryExistsException("No category with id: "+ categoryId));

        Optional.ofNullable(categoryToUpdate.getCategoryType()).ifPresent(resp::setCategoryType);
        Optional.ofNullable(categoryToUpdate.getDescription()).ifPresent(resp::setDescription);
        Optional.ofNullable(categoryToUpdate.getName()).ifPresent(resp::setName);

        resp.setUpdatedAt(OffsetDateTime.now());

        CategoryEntity savedCategory = categoryRepository.save(resp);

        return categoryMapper.fromCategoryEntityToCategoryInformation(savedCategory);
    }

    public FetchAllCategoriesResponse getAllCategories(UUID userId){
        List<CategoryEntity> listOfUserCategories = categoryRepository.fetchUsersAndDefaultCategories(true, userId);
        return new FetchAllCategoriesResponse(categoryMapper.fromListOfCategoryEntityToListOfCategoryInformation(listOfUserCategories));
    }


}
