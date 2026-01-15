package io.qozz.qozzbank.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "card_limit_configs",
        indexes = {
                @Index(name = "idx_card_limit_configs_card_id", columnList = "card_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_card_limit_configs_type", columnNames = {"card_id", "limit_type"})
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CardLimitConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "card_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_card_limit_configs_card_id")
    )
    private CardEntity card;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "limit_type", nullable = false, length = 50)
    private String limitType;

    @Column(name = "max_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal maxAmount;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
