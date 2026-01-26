package io.qozz.qozzbank.controller;

import io.qozz.qozzbank.config.RabbitMQConfig;
import io.qozz.qozzbank.containers.PostgresContainerConfig;
import io.qozz.qozzbank.containers.RabbitContainerConfig;
import io.qozz.qozzbank.domain.entity.AccountEntity;
import io.qozz.qozzbank.domain.entity.UserEntity;
import io.qozz.qozzbank.domain.enumeration.*;
import io.qozz.qozzbank.repository.AccountRepository;
import io.qozz.qozzbank.repository.TransactionRepository;
import io.qozz.qozzbank.repository.UserRepository;
import io.qozz.qozzbank.service.dto.TransferResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.model.TransferRequest;
import org.openapitools.model.TransferResponse;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ImportTestcontainers({PostgresContainerConfig.class, RabbitContainerConfig.class})
public class TransferControllerIT {
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


    private RestTestClient restTestClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
        rabbitAdmin.purgeQueue(RabbitMQConfig.QUEUE_TRANSFER);

        restTestClient = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    @DisplayName("POST /api/v1/transfers - Should return 201 and PENDING status when request is valid")
    void shouldCreateTransferSuccessfully() {
        UserEntity userFrom = userRepository.save(createUserEntity(FIRST_NAME_YODA, LAST_NAME_YODA, UserStatus.ACTIVE));
        UserEntity userTo = userRepository.save(createUserEntity(FIRST_NAME_DART_VADER, LAST_NAME_VADER_VADER, UserStatus.ACTIVE));

        AccountEntity fromAccount = accountRepository.save(createAccountEntity(userFrom, IBAN_USD_1, CURRENCY_USD, BALANCE_100_000));
        AccountEntity toAccount = accountRepository.save(createAccountEntity(userTo, IBAN_USD_2, CURRENCY_USD, BALANCE_100_000));

        var request = new TransferRequest()
                .fromAccountId(fromAccount.getId())
                .toAccountId(toAccount.getId())
                .amount(new BigDecimal("500.0000"))
                .description("Payment for service");

        restTestClient.post()
                .uri("/api/v1/transfers")
                .header("X-Correlation-ID", CORRELATION_ID.toString())
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(TransferResponse.class)
                .consumeWith(body -> {
                    TransferResponse response = body.getResponseBody();
                    Assertions.assertEquals(TransactionStatus.PENDING, TransactionStatus.fromValue(response.getStatus()));
                });

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

}
