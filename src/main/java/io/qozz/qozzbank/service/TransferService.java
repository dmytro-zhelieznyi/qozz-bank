package io.qozz.qozzbank.service;

import io.qozz.qozzbank.domain.entity.AccountEntity;
import io.qozz.qozzbank.domain.entity.TransactionEntity;
import io.qozz.qozzbank.domain.enumeration.TransactionStatus;
import io.qozz.qozzbank.domain.enumeration.TransactionType;
import io.qozz.qozzbank.messaging.event.TransferCreatedEvent;
import io.qozz.qozzbank.repository.AccountRepository;
import io.qozz.qozzbank.repository.TransactionRepository;
import io.qozz.qozzbank.service.dto.transaction.TransferResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.TransferRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransferResult createTransfer(
            UUID correlationId,
            // TODO implement when test for the flow will be ready
            UUID idempotencyKey,
            @Valid TransferRequest request
    ) {
        log.info("[{}] [SERVICE_START] Looking up accounts for transfer. From: [{}], To: [{}]",
                correlationId, request.getFromIban(), request.getToIban());

        AccountEntity fromAccount = accountRepository.findByIbanWithPessimisticLock(request.getFromIban())
                .orElseThrow(() -> {
                    log.error("[{}] [SERVICE_ERROR] Source account not found: [{}]", correlationId, request.getFromIban());
                    return new RuntimeException("Source account not found");
                });

        AccountEntity toAccount = accountRepository.findByIban(request.getToIban())
                .orElseThrow(() -> {
                    log.error("[{}] [SERVICE_ERROR] Destination account not found: [{}]", correlationId, request.getToIban());
                    return new RuntimeException("Destination account not found");
                });

        if (fromAccount.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            log.warn("[{}] [SERVICE_VAL_FAIL] Insufficient funds. Account: [{}], Available: [{}], Required: [{}]",
                    correlationId, fromAccount.getId(), fromAccount.getAvailableBalance(), request.getAmount());
            throw new RuntimeException("Not enough available balance");
        }

        fromAccount.setAvailableBalance(fromAccount.getAvailableBalance().subtract(request.getAmount()));
        accountRepository.save(fromAccount);
        log.info("[{}] [SERVICE_RESERVE] Funds reserved. New available balance: [{}]",
                correlationId, fromAccount.getAvailableBalance());

        UUID operationId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        TransactionEntity pendingDebit = TransactionEntity.builder()
                .operationId(operationId)
                .account(fromAccount)
                .relatedCard(null)
                .amount(request.getAmount().negate())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .createdAt(now)
                .build();

        TransactionEntity pendingCredit = TransactionEntity.builder()
                .operationId(operationId)
                .account(toAccount)
                .relatedCard(null)
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .createdAt(now)
                .build();

        transactionRepository.save(pendingDebit);
        transactionRepository.save(pendingCredit);
        log.info("[{}] [SERVICE_DB_SAVE] Pending transactions saved. OperationId: [{}]", correlationId, operationId);

        TransferCreatedEvent transferCreatedEvent = new TransferCreatedEvent(
                correlationId,
                operationId,
                fromAccount.getId(),
                toAccount.getId(),
                request.getAmount(),
                now
        );
        eventPublisher.publishEvent(transferCreatedEvent);
        log.info("[{}] [SERVICE_EVENT_PUBLISHED] Internal event published for operation: [{}]", correlationId, operationId);

        return new TransferResult(
                operationId,
                TransactionStatus.PENDING,
                now
        );
    }

}
