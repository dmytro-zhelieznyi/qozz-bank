package io.qozz.qozzbank.service;

import io.qozz.qozzbank.domain.entity.AccountEntity;
import io.qozz.qozzbank.domain.entity.TransactionEntity;
import io.qozz.qozzbank.domain.enumeration.TransactionStatus;
import io.qozz.qozzbank.domain.enumeration.TransactionType;
import io.qozz.qozzbank.mapper.TransactionMapper;
import io.qozz.qozzbank.messaging.event.TransferCreatedEvent;
import io.qozz.qozzbank.repository.AccountRepository;
import io.qozz.qozzbank.repository.TransactionRepository;
import io.qozz.qozzbank.service.dto.transaction.TransactionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.AccountsTransactionRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransactionDto createTransaction(
            UUID correlationId,
            UUID idempotencyKey,
            @Valid AccountsTransactionRequest request
    ) {
        AccountEntity fromAccount = accountRepository.findByIbanWithPessimisticLock(request.getFromIban())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        AccountEntity toAccount = accountRepository.findByIban(request.getToIban())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (fromAccount.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Not enough available balance");
        }

        fromAccount.setAvailableBalance(fromAccount.getAvailableBalance().subtract(request.getAmount()));
        accountRepository.save(fromAccount);

        UUID operationId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        TransactionEntity fromTransaction = TransactionEntity.builder()
                .operationId(operationId)
                .account(fromAccount)
                .fromIban(fromAccount.getIban())
                .toIban(toAccount.getIban())
                .relatedCard(null)
                .amount(request.getAmount().negate())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .createdAt(now)
                .build();

        TransactionEntity toTransaction = TransactionEntity.builder()
                .operationId(operationId)
                .account(toAccount)
                .fromIban(fromAccount.getIban())
                .toIban(toAccount.getIban())
                .relatedCard(null)
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .createdAt(now)
                .build();

        TransactionEntity savedDebit = transactionRepository.save(fromTransaction);
        transactionRepository.save(toTransaction);

        TransferCreatedEvent transferCreatedEvent = new TransferCreatedEvent(
                correlationId,
                operationId,
                fromAccount.getId(),
                toAccount.getId(),
                request.getAmount(),
                now
        );
        eventPublisher.publishEvent(transferCreatedEvent);

        return transactionMapper.toDto(savedDebit);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getAccountTransactions(String iban) {
        List<TransactionEntity> transactions = transactionRepository.findByAccountIban(iban);

        return transactions.stream()
                .collect(Collectors.toMap(
                        TransactionEntity::getOperationId,
                        t -> t,
                        (existing, replacement) ->
                                replacement.getCreatedAt().isAfter(existing.getCreatedAt())
                                        ? replacement : existing
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(TransactionEntity::getCreatedAt).reversed())
                .map(transactionMapper::toDto)
                .toList();
    }
}
