package com.monetrax.monetrax.categories.mapper;

import com.monetrax.monetrax.categories.dto.CategoryCreate;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public CategoryInformation fromCategoryEntityToCategoryInformation(CategoryEntity categoryEntity){
        return CategoryInformation.builder()
                .categoryId(categoryEntity.getCategoryId())
                .categoryType(categoryEntity.getCategoryType())
                .name(categoryEntity.getName())
                .description(categoryEntity.getDescription())
                .defaultCategory(categoryEntity.isDefaultCategory())
                .build();
    }

    public CategoryEntity fromCategoryCreateToCategoryEntity(CategoryCreate toCreate, UserEntity userEntity){
        return CategoryEntity.builder()
                .categoryType(toCreate.getCategoryType())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .description(toCreate.getDescription()==null? "No description provided.": toCreate.getDescription())
                .name(toCreate.getName())
                .defaultCategory(false)
                .user(userEntity)
                .build();
    }

    public List<CategoryInformation> fromListOfCategoryEntityToListOfCategoryInformation(List<CategoryEntity> entityList){
        return entityList.stream()
                .map(this::fromCategoryEntityToCategoryInformation)
                .collect(Collectors.toList());
    }

}

