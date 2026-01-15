package io.qozz.qozzbank.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "card_limit_counters",
        indexes = {
                @Index(name = "idx_card_limit_counters_card_id", columnList = "card_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_card_limit_counters_type", columnNames = {"card_id", "limit_type"})
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CardLimitCounterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "limit_type", nullable = false, length = 50)
    private String limitType;

    @Builder.Default
    @Column(name = "current_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "reset_date", nullable = false)
    private OffsetDateTime resetDate;
}
