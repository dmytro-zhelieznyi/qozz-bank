package io.qozz.qozzbank.controller;

import io.qozz.qozzbank.domain.entity.UserEntity;
import io.qozz.qozzbank.mapper.AccountMapper;
import io.qozz.qozzbank.mapper.TransactionMapper;
import io.qozz.qozzbank.security.context.UserContext;
import io.qozz.qozzbank.service.AccountService;
import io.qozz.qozzbank.service.TransactionService;
import io.qozz.qozzbank.service.dto.account.AccountDto;
import io.qozz.qozzbank.service.dto.transaction.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.AccountsApi;
import org.openapitools.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountsController implements AccountsApi {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public ResponseEntity<AccountsResponse> getAccounts(UUID xCorrelationID) {
        List<AccountDto> accountDtos = accountService.findUserAccounts();
        List<AccountData> accountsData = accountDtos.stream()
                .map(accountMapper::toAccountData)
                .toList();
        AccountsResponse response = new AccountsResponse().accounts(accountsData);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccount(UUID xCorrelationID, String iban) {
        AccountDto accountDto = accountService.findAccountByIban(iban);
        AccountData accountData = accountMapper.toAccountData(accountDto);
        AccountResponse response = new AccountResponse().account(accountData);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TransactionsResponse> getAccountTransactions(UUID xCorrelationID, String iban) {
        List<TransactionDto> transactionDtos = transactionService.getAccountTransactions(iban);
        List<TransactionData> transactionsData = transactionDtos.stream()
                .map(transactionMapper::toTransactionData)
                .toList();
        TransactionsResponse response = new TransactionsResponse().transactions(transactionsData);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountResponse> createAccount(UUID xCorrelationID, AccountRequest accountRequest) {
        AccountDto accountDto = accountService.createAccount(accountRequest);
        AccountData accountData = accountMapper.toAccountData(accountDto);
        AccountResponse response = new AccountResponse().account(accountData);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
