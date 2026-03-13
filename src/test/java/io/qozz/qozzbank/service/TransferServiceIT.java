package io.qozz.qozzbank.service;

import io.qozz.qozzbank.config.RabbitMQConfig;
import io.qozz.qozzbank.containers.PostgresContainerConfig;
import io.qozz.qozzbank.containers.RabbitContainerConfig;
import io.qozz.qozzbank.domain.entity.AccountEntity;
import io.qozz.qozzbank.domain.entity.TransactionEntity;
import io.qozz.qozzbank.domain.entity.UserEntity;
import io.qozz.qozzbank.domain.enumeration.*;
import io.qozz.qozzbank.repository.AccountRepository;
import io.qozz.qozzbank.repository.TransactionRepository;
import io.qozz.qozzbank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.model.TransferRequest;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@ImportTestcontainers({PostgresContainerConfig.class, RabbitContainerConfig.class})
class TransferServiceIT {
    private static final String FIRST_NAME_YODA = "Yoda";
    private static final String LAST_NAME_YODA = "Master";
    private static final String FIRST_NAME_DART_VADER = "Darth";
    private static final String LAST_NAME_VADER_VADER = "Vader";

    private static final String IBAN_USD_1 = "US01QOZZ1000000000000001";
    private static final String IBAN_USD_2 = "US01QOZZ1000000000000002";
    private static final String IBAN_USD_3 = "US01QOZZ1000000000000003";
    private static final String IBAN_USD_4 = "US01QOZZ1000000000000004";

    private static final String CURRENCY_USD = "USD";
    private static final BigDecimal BALANCE_100_000 = new BigDecimal("100000.0000");

    private static final UUID CORRELATION_ID = UUID.randomUUID();

    @Autowired
    private TransactionService transferService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        rabbitAdmin.purgeQueue(RabbitMQConfig.QUEUE_TRANSFER);
    }

    @Test
    @DisplayName("Should Complete Full Transfer Flow and Maintain Immutable Audit Log with 2 PENDING and 2 COMPLETED transactions")
    void shouldCompleteFullTransferFlowAndMaintainImmutableAuditLog() {
        BigDecimal transferAmount = new BigDecimal("500.0000");
        BigDecimal expectedBalanceAccountFrom = BALANCE_100_000.subtract(transferAmount);
        BigDecimal expectedAvailableBalanceAccountFrom = BALANCE_100_000.subtract(transferAmount);
        BigDecimal expectedBalanceAccountTo = BALANCE_100_000.add(transferAmount);
        BigDecimal expectedAvailableBalanceAccountTo = BALANCE_100_000.add(transferAmount);

        UserEntity userFrom = userRepository.save(createUserEntity(FIRST_NAME_YODA, LAST_NAME_YODA, UserStatus.ACTIVE));
        UserEntity userTo = userRepository.save(createUserEntity(FIRST_NAME_DART_VADER, LAST_NAME_VADER_VADER, UserStatus.ACTIVE));

        AccountEntity fromAccount = accountRepository.save(createAccountEntity(userFrom, IBAN_USD_1, CURRENCY_USD, BALANCE_100_000));
        AccountEntity toAccount = accountRepository.save(createAccountEntity(userTo, IBAN_USD_2, CURRENCY_USD, BALANCE_100_000));

        TransferRequest transferRequest = new TransferRequest()
                .fromAccountId(fromAccount.getId())
                .toAccountId(toAccount.getId())
                .amount(transferAmount)
                .description("Just money transfer");

        var result = transferService.createTransaction(CORRELATION_ID, null, transferRequest);

        await()
                .atMost(2, TimeUnit.SECONDS)
                .pollInterval(50, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var transactions = transactionRepository.findByOperationId(result.operationId());
                    assertTrue(transactions.size() >= 2, "Should be created 2 PENDING transaction");

                    List<TransactionEntity> pendingTransactions = transactions.stream()
                            .filter(t -> TransactionStatus.PENDING == t.getStatus())
                            .toList();

                    TransactionEntity fromAccountPendingTransaction = getTransactionByStatusAndAccountId(pendingTransactions, TransactionStatus.PENDING, fromAccount.getId());
                    assertEquals(TransactionStatus.PENDING, fromAccountPendingTransaction.getStatus());
                    assertEquals(transferAmount.negate(), fromAccountPendingTransaction.getAmount());

                    TransactionEntity toAccountPendingTransaction = getTransactionByStatusAndAccountId(pendingTransactions, TransactionStatus.PENDING, toAccount.getId());
                    assertEquals(TransactionStatus.PENDING, toAccountPendingTransaction.getStatus());
                    assertEquals(transferAmount, toAccountPendingTransaction.getAmount());

                    var fromAccountDb = accountRepository.findById(fromAccount.getId()).orElseThrow();
                    assertEquals(BALANCE_100_000.subtract(transferAmount), fromAccountDb.getAvailableBalance());
                    assertEquals(BALANCE_100_000.subtract(transferAmount), fromAccountDb.getBalance());
                });

        await()
                .atMost(3, TimeUnit.SECONDS)
                .with()
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    var transactions = transactionRepository.findByOperationId(result.operationId());
                    assertEquals(4, transactions.size(), "Should be created 4 transactions (2 PENDING + 2 COMPLETED)");

                    List<TransactionEntity> completedTransactions = transactions.stream()
                            .filter(t -> TransactionStatus.COMPLETED == t.getStatus())
                            .toList();

                    TransactionEntity fromAccountPendingTransaction = getTransactionByStatusAndAccountId(completedTransactions, TransactionStatus.COMPLETED, fromAccount.getId());
                    assertEquals(TransactionStatus.COMPLETED, fromAccountPendingTransaction.getStatus());
                    assertEquals(transferAmount.negate(), fromAccountPendingTransaction.getAmount());

                    TransactionEntity toAccountPendingTransaction = getTransactionByStatusAndAccountId(completedTransactions, TransactionStatus.COMPLETED, toAccount.getId());
                    assertEquals(TransactionStatus.COMPLETED, toAccountPendingTransaction.getStatus());
                    assertEquals(transferAmount, toAccountPendingTransaction.getAmount());

                    var fromAccountDb = accountRepository.findById(fromAccount.getId()).orElseThrow();
                    assertEquals(expectedAvailableBalanceAccountFrom, fromAccountDb.getAvailableBalance());
                    assertEquals(expectedBalanceAccountFrom, fromAccountDb.getBalance());

                    var toAccountDb = accountRepository.findById(toAccount.getId()).orElseThrow();
                    assertEquals(expectedAvailableBalanceAccountTo, toAccountDb.getAvailableBalance());
                    assertEquals(expectedBalanceAccountTo, toAccountDb.getBalance());
                });
    }

    @Test
    @DisplayName("Should maintain total system balance integrity under high-concurrency stress test with circular transaction patterns")
    void shouldHandleConcurrentTransfersWithoutDataLoss() throws InterruptedException {
        BigDecimal transferAmount = new BigDecimal("10.0000");
        BigDecimal expectedBalance = BALANCE_100_000;
        BigDecimal expectedAvailableBalance = BALANCE_100_000;
        BigDecimal expectedTotal = BALANCE_100_000.multiply(BigDecimal.valueOf(4));


        UserEntity user1 = userRepository.save(createUserEntity(FIRST_NAME_YODA, LAST_NAME_YODA, UserStatus.ACTIVE));
        UserEntity user2 = userRepository.save(createUserEntity(FIRST_NAME_DART_VADER, LAST_NAME_VADER_VADER, UserStatus.ACTIVE));

        AccountEntity acc1 = accountRepository.save(createAccountEntity(user1, IBAN_USD_1, CURRENCY_USD, BALANCE_100_000));
        AccountEntity acc2 = accountRepository.save(createAccountEntity(user1, IBAN_USD_2, CURRENCY_USD, BALANCE_100_000));
        AccountEntity acc3 = accountRepository.save(createAccountEntity(user1, IBAN_USD_3, CURRENCY_USD, BALANCE_100_000));
        AccountEntity acc4 = accountRepository.save(createAccountEntity(user2, IBAN_USD_4, CURRENCY_USD, BALANCE_100_000));

        int transactionsPerRoute = 25;
        int totalTasks = transactionsPerRoute * 6;

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(totalTasks);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < transactionsPerRoute; i++) {
                submitTransfer(executor, acc1, acc4, transferAmount, startGate, doneGate); // A1 > A4
                submitTransfer(executor, acc2, acc4, transferAmount, startGate, doneGate); // A2 > A4
                submitTransfer(executor, acc3, acc4, transferAmount, startGate, doneGate); // A3 > A4

                submitTransfer(executor, acc4, acc1, transferAmount, startGate, doneGate); // A4 > A1
                submitTransfer(executor, acc4, acc2, transferAmount, startGate, doneGate); // A4 > A2
                submitTransfer(executor, acc4, acc3, transferAmount, startGate, doneGate); // A4 > A3
            }

            startGate.countDown();
            boolean finished = doneGate.await(10, TimeUnit.SECONDS);
            assertTrue(finished, "Not all transactions finished within timeout");
        }

        await()
                .atMost(15, TimeUnit.SECONDS)
                .with()
                .pollDelay(3, TimeUnit.SECONDS)
                .pollInterval(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var final1 = accountRepository.findById(acc1.getId()).orElseThrow();
                    var final2 = accountRepository.findById(acc2.getId()).orElseThrow();
                    var final3 = accountRepository.findById(acc3.getId()).orElseThrow();
                    var final4 = accountRepository.findById(acc4.getId()).orElseThrow();

                    BigDecimal totalBalance = countAccountsTotalBalance(List.of(final1, final2, final3, final4));
                    BigDecimal totalAvailableBalance = countAccountsTotalAvailableBalance(List.of(final1, final2, final3, final4));

                    assertEquals(0, expectedTotal.compareTo(totalBalance), "Total Balance mismatch: " + totalBalance);
                    assertEquals(0, expectedTotal.compareTo(totalAvailableBalance), "Available Balance mismatch: " + totalAvailableBalance);

                    List.of(final1, final2, final3, final4).forEach(acc -> {
                        assertEquals(0, expectedBalance.compareTo(acc.getBalance()));
                        assertEquals(0, expectedAvailableBalance.compareTo(acc.getAvailableBalance()));
                    });
                });
    }

    private void submitTransfer(ExecutorService executor, AccountEntity from, AccountEntity to,
                                BigDecimal transferAmount,
                                CountDownLatch startGate, CountDownLatch doneGate) {
        executor.submit(() -> {
            try {
                startGate.await();
                transferService.createTransaction(UUID.randomUUID(), null,
                        new TransferRequest()
                                .fromAccountId(from.getId())
                                .toAccountId(to.getId())
                                .amount(transferAmount)
                                .description("Cross transfer"));
            } catch (Exception e) {
                System.err.println("🔄 Transaction " + from.getIban() + " -> " + to.getIban() + " failed: " + e.getMessage());
            } finally {
                doneGate.countDown();
            }
        });
    }

    private TransactionEntity getTransactionByStatusAndAccountId(List<TransactionEntity> pendingTransactions, TransactionStatus transactionStatus, UUID id) {
        Optional<TransactionEntity> transactionOpt = pendingTransactions.stream()
                .filter(t -> transactionStatus == t.getStatus() && id.equals(t.getAccount().getId()))
                .findAny();

        return transactionOpt.orElseThrow();
    }

    private UserEntity createUserEntity(String firstName, String lastName, UserStatus status) {
        return UserEntity.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(String.format("%s.%s@qozzbank.io", firstName, lastName))
                .status(status)
                .build();
    }

    private AccountEntity createAccountEntity(UserEntity user, String iban, String currency, BigDecimal balance) {
        return AccountEntity.builder()
                .user(user)
                .iban(iban)
                .currency(currency)
                .balance(balance)
                .availableBalance(BALANCE_100_000)
                .accountType(AccountType.CHECKING)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private BigDecimal countAccountsTotalBalance(List<AccountEntity> accounts) {
        return calculateTotal(accounts, AccountEntity::getBalance);
    }

    private BigDecimal countAccountsTotalAvailableBalance(List<AccountEntity> accounts) {
        return calculateTotal(accounts, AccountEntity::getAvailableBalance);
    }

    private BigDecimal calculateTotal(List<AccountEntity> accounts, Function<AccountEntity, BigDecimal> mapper) {
        return accounts.stream()
                .map(mapper)
                .filter(Objects::nonNull) // Игнорируем null значения
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
