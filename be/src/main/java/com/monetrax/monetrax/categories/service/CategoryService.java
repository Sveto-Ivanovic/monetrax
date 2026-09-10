package com.monetrax.monetrax.categories.service;

import com.monetrax.monetrax.categories.dto.*;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryInformation createCategory(CategoryCreate category, UUID userId);
    CategoryInformation getCategory(UUID categoryId, UUID userId);
    CategoryDeletionSuccess deleteCategory(UUID categoryId, UUID userId);
    CategoryInformation updateCategory(CategoryUpdate categoryToUpdate, UUID categoryId, UUID userId);
    FetchAllCategoriesResponse getAllCategories(UUID userId);
}
