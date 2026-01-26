package io.qozz.qozzbank.messaging.listener;

import io.qozz.qozzbank.config.RabbitMQConfig;
import io.qozz.qozzbank.domain.entity.AccountEntity;
import io.qozz.qozzbank.domain.entity.TransactionEntity;
import io.qozz.qozzbank.domain.enumeration.TransactionStatus;
import io.qozz.qozzbank.messaging.event.TransferCreatedEvent;
import io.qozz.qozzbank.repository.AccountRepository;
import io.qozz.qozzbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferEventListener {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @RabbitListener(
            queues = RabbitMQConfig.QUEUE_TRANSFER,
            concurrency = "5-10"
    )
    @Transactional
    public void handleTransferEvent(TransferCreatedEvent event) {
        log.info("Finalizing transfer for Operation ID: {}, event: [{}]", event.operationId(), event);
        List<TransactionEntity> pendingTxs = transactionRepository.findByOperationId(event.operationId());

        boolean containsNotPendingTransaction = pendingTxs.stream()
                .anyMatch(t -> t.getStatus() != (TransactionStatus.PENDING));

        if (containsNotPendingTransaction) {
            String txDetails = pendingTxs.stream()
                    .map(t -> String.format("\n[ID: %s, Status: %s]", t.getId(), t.getStatus()))
                    .collect(Collectors.joining(", "));

            log.error("Conflict detected! Operation ID: {}. Non-pending transactions: {}", event.operationId(), txDetails);

            throw new RuntimeException(String.format(
                    "Cannot finalize Operation ID %s. Transactions already processed: %s",
                    event.operationId(), txDetails
            ));
        }

        pendingTxs.stream()
                .sorted(Comparator.comparing(t -> t.getAccount().getId()))
                .forEach(pendingTx -> {
                    log.info("Update account: {}", pendingTx.getAccount().getId());
                    AccountEntity account = accountRepository.findByIdWithPessimisticLock(pendingTx.getAccount().getId())
                            .orElseThrow(() -> new RuntimeException("Account not found: " + pendingTx.getAccount().getId()));
                    BigDecimal amount = pendingTx.getAmount();

                    // toAccount transaction
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        log.info("Update balance (credit): {}", pendingTx.getAccount().getId());
                        account.setBalance(account.getBalance().add(amount));
                        account.setAvailableBalance(account.getAvailableBalance().add(amount));
                    } else { // fromAccount transaction
                        log.info("Update balance (debit): {}", pendingTx.getAccount().getId());
                        account.setBalance(account.getBalance().add(amount));
                    }

                    log.info("Save account changes: {}", pendingTx.getAccount().getId());
                    accountRepository.save(account);

                    TransactionEntity completedTx = TransactionEntity.builder()
                            .operationId(pendingTx.getOperationId())
                            .account(account)
                            .amount(amount)
                            .type(pendingTx.getType())
                            .status(TransactionStatus.COMPLETED)
                            .description("Finalized: " + pendingTx.getDescription())
                            .createdAt(OffsetDateTime.now())
                            .relatedCard(pendingTx.getRelatedCard())
                            .build();

                    log.info("Create completed transaction account: {}", completedTx.getId());
                    transactionRepository.save(completedTx);
                    log.info("Created COMPLETED record for account: {}", account.getId());
                });
        log.info("Transfer operation {} finalized with new immutable records.", event.operationId());
    }
}
