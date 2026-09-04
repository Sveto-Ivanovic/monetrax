package com.monetrax.monetrax.categories.repository;

import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository  extends JpaRepository<CategoryEntity, UUID> {

    @Query("select e from CategoryEntity e where e.defaultCategory = ?1 or e.user.userId = ?2")
    public List<CategoryEntity> fetchUsersAndDefaultCategories(boolean isDefault, UUID userId);

    @Query("select e from CategoryEntity e where e.categoryId = ?1 and e.user.userId = ?2")
    public Optional<CategoryEntity> fetchUsersCategory(UUID categoryId, UUID userId);
}
