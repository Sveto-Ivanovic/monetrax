package com.monetrax.monetrax.categories.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "categories_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoryEntity {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID categoryId;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryKind categoryType;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = true)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private OffsetDateTime updatedAt;

    @Column(name = "is_default")
    private boolean isDefault;
}

