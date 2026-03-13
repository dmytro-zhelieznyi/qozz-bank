package io.qozz.qozzbank.controller;

import io.qozz.qozzbank.domain.entity.TransactionEntity;
import io.qozz.qozzbank.mapper.TransactionMapper;
import io.qozz.qozzbank.service.TransactionService;
import io.qozz.qozzbank.service.dto.transaction.TransactionDto;
import io.qozz.qozzbank.service.dto.transaction.TransferResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.TransactionsApi;
import org.openapitools.model.TransactionData;
import org.openapitools.model.AccountsTransactionRequest;
import org.openapitools.model.TransactionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController implements TransactionsApi {
    private final TransactionService transferService;
    private final TransactionMapper transactionMapper;

    @Override
    public ResponseEntity<TransactionResponse> createTransaction(UUID xCorrelationID,
                                                                 UUID idempotencyKey,
                                                                 AccountsTransactionRequest request) {
        TransactionDto transactionDto = transferService.createTransaction(
                xCorrelationID,
                idempotencyKey,
                request
        );
        TransactionData transactionData = transactionMapper.toTransactionData(transactionDto);
        TransactionResponse response = new TransactionResponse().transaction(transactionData);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

