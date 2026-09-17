package com.monetrax.monetrax.alerts.entity;

import com.monetrax.monetrax.categories.entity.CategoryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alert_conditions")
public class AlertConditionEntity {
    @Id
    @Column(name = "condition_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID conditionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    private AlertEntity alert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;

    @Column(name = "limit_value_low_or_equal", nullable = false, precision = 14, scale = 2)
    private BigDecimal limitValueLowOrEqual;

    @Column(name = "limit_value_high", precision = 14, scale = 2)
    private BigDecimal limitValueHigh;
}
