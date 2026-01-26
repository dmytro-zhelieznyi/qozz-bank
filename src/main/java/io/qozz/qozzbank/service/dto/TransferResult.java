package io.qozz.qozzbank.service.dto;


import io.qozz.qozzbank.domain.enumeration.TransactionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TransferResult(
        UUID operationId,
        TransactionStatus status,
        OffsetDateTime createdAt
) {
}

