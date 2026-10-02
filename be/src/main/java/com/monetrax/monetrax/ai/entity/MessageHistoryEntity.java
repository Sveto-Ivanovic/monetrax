package com.monetrax.monetrax.ai.entity;

import com.monetrax.monetrax.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id")
    private UUID messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id")
    private ApiKeysEntity apiKey;

    @Column(name = "provider")
    private String provider;

    @Column(name = "model")
    private String model;

    // TEXT in Postgres, CLOB in H2.
    @Column(name = "raw_message", columnDefinition = "TEXT")
    private String rawMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "structured_output")
    private String structuredOutput;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "input_tokens")
    private Integer inputTokens;

    @Column(name = "output_tokens")
    private Integer outputTokens;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
