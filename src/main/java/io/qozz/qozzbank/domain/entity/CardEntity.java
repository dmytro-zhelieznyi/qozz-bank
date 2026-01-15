package io.qozz.qozzbank.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "cards",
        indexes = {
                @Index(name = "idx_cards_account_id", columnList = "account_id")
        })
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "account_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cards_account_id")
    )
    private AccountEntity account;

    @Column(name = "masked_pan", nullable = false, length = 19)
    private String maskedPan;

    @Column(name = "expiry_date", nullable = false, updatable = false)
    private LocalDate expiryDate;

    @Column(name = "card_holder_name", nullable = false, length = 200)
    private String cardHolderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
