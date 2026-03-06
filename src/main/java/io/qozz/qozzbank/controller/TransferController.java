package io.qozz.qozzbank.controller;

import io.qozz.qozzbank.service.TransferService;
import io.qozz.qozzbank.service.dto.transaction.TransferResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.TransactionsApi;
import org.openapitools.model.TransferRequest;
import org.openapitools.model.TransferResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransferController implements TransactionsApi {
    private final TransferService transferService;

    @Override
    public ResponseEntity<TransferResponse> createTransfer(
            UUID correlationId,
            UUID idempotencyKey,
            TransferRequest request
    ) {
        log.info("[{}] [TRANSFER_START] From Iban: [{}], Amount: [{}],  To Iban: [{}]",
                correlationId, request.getFromIban(), request.getAmount(), request.getFromIban());

        TransferResult result = transferService.createTransfer(
                correlationId,
                idempotencyKey,
                request
        );

        TransferResponse response = mapToTransferResponse(result);

        log.info("[{}] [TRANSFER_SUCCESS] OperationId: [{}]", correlationId, result.operationId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private TransferResponse mapToTransferResponse(TransferResult result) {
        return new TransferResponse(
                result.operationId(),
                result.status().getValue(),
                result.createdAt()
        );
    }
}

