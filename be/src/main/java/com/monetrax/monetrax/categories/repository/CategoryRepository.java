package com.monetrax.monetrax.categories.repository;

import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository  extends JpaRepository<CategoryEntity, UUID> {
}
