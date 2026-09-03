package com.monetrax.monetrax.categories.service.impl;

import com.monetrax.monetrax.categories.dto.CategoryCreate;
import com.monetrax.monetrax.categories.dto.CategoryDeletionSuccess;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.dto.CategoryUpdate;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.exceptions.NoSuchCategoryExistsException;
import com.monetrax.monetrax.categories.mapper.CategoryMapper;
import com.monetrax.monetrax.categories.repository.CategoryRepository;
import com.monetrax.monetrax.categories.service.CategoryService;
import com.monetrax.monetrax.common.exception.shared.UnauthorizedAccess;
import com.monetrax.monetrax.user.exception.NoSuchUserExistsException;

import java.util.List;
import java.util.UUID;

public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper){
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryInformation getCategory(UUID categoryId, UUID userId){
        CategoryEntity resp = categoryRepository.findById(categoryId).orElseThrow(()->new NoSuchCategoryExistsException("No category with id: "+ categoryId));
        if(!resp.isDefault()&&!userId.equals(resp.getUserId())){
            throw new UnauthorizedAccess("You cannot access this the category!");
        }
        return categoryMapper.fromCategoryEntityToCategoryInformation(resp);
    }

    public CategoryInformation createCategory(CategoryCreate category, UUID userId){
        return null;
    }

    public CategoryDeletionSuccess deleteCategory(UUID categoryId, UUID userId){
        return null;
    }

    public CategoryInformation updateCategory(CategoryUpdate categoryToUpdate, UUID userId){
        return null;
    }

    public List<CategoryInformation> getAllCategories(UUID userId){
        return null;
    }


}
