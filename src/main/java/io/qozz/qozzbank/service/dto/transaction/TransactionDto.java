package io.qozz.qozzbank.service.dto.transaction;

import io.qozz.qozzbank.domain.enumeration.TransactionStatus;
import io.qozz.qozzbank.domain.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionDto(
        UUID id,
        UUID operationId,
        String fromIban,
        String toIban,
        BigDecimal amount,
        TransactionType type,
        TransactionStatus status,
        String description,
        String referenceId,
        UUID relatedCardId,
        OffsetDateTime createdAt
) {}