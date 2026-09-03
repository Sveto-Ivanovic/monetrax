package com.monetrax.monetrax.categories.mapper;

import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryInformation fromCategoryEntityToCategoryInformation(CategoryEntity categoryEntity){
        return CategoryInformation.builder()
                .categoryId(categoryEntity.getCategoryId())
                .categoryType(categoryEntity.getCategoryType())
                .name(categoryEntity.getName())
                .description(categoryEntity.getDescription())
                .isDefault(categoryEntity.isDefault())
                .build();
    }

}

