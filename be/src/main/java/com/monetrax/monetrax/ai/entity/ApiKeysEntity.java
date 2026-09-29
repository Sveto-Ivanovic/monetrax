package com.monetrax.monetrax.ai.entity;

import com.monetrax.monetrax.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiKeysEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "key_id")
    private UUID keyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "key_type")
    private KeyType keyType;

    @Column(name = "api_key_encrypted", length = 2048)
    private String apiKeyEncrypted;

    @Column(name = "key_last4", length = 4)
    private String keyLast4;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
