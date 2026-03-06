package io.qozz.qozzbank.service.dto.account;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountDto(
        UUID id,
        UUID userId,
        String iban,
        String currency,
        BigDecimal balance,
        BigDecimal availableBalance,
        String accountType,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}