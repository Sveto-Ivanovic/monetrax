package com.monetrax.monetrax.categories.service;

import com.monetrax.monetrax.categories.dto.CategoryCreate;
import com.monetrax.monetrax.categories.dto.CategoryDeletionSuccess;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.dto.CategoryUpdate;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryInformation createCategory(CategoryCreate category, UUID userId);
    CategoryInformation getCategory(UUID categoryId, UUID userId);
    CategoryDeletionSuccess deleteCategory(UUID categoryId, UUID userId);
    CategoryInformation updateCategory(CategoryUpdate categoryToUpdate, UUID userId);
    List<CategoryInformation> getAllCategories(UUID userId);
}
