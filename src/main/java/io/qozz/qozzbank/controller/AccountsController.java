package io.qozz.qozzbank.controller;

import io.qozz.qozzbank.mapper.AccountMapper;
import io.qozz.qozzbank.service.AccountService;
import io.qozz.qozzbank.service.dto.account.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.AccountsApi;
import org.openapitools.model.AccountRequest;
import org.openapitools.model.AccountResponse;
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
    private final AccountMapper accountMapper;

    @Override
    public ResponseEntity<List<AccountResponse>> getAccountsByAuthId(UUID id) {
        log.info("[USER_ACCOUNTS_GET_START] AuthId: [{}]", id);

        List<AccountDto> result = accountService.findUserAccountsById(id);

        List<AccountResponse> response = result.stream()
                .map(accountMapper::toResponse)
                .toList();

        log.info("[USER_ACCOUNTS_GET_SUCCESS] AuthId: [{}]", id);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountByIban(String iban) {
        log.info("[ACCOUNT_GET_START] iban: [{}]", iban);

        AccountDto result = accountService.findAccountByIban(iban);

        AccountResponse response = accountMapper.toResponse(result);

        log.info("[ACCOUNT_GET_SUCCESS] AuthId: [{}]", iban);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AccountResponse> createAccount(AccountRequest accountRequest) {
        log.info("[ACCOUNT_CREATE_START] Creating account for AuthId: [{}] type: [{}]",
                accountRequest.getAuthId(), accountRequest.getAccountType());

        AccountDto createdAccount = accountService.createAccount(accountRequest);

        AccountResponse response = accountMapper.toResponse(createdAccount);

        log.info("[ACCOUNT_CREATE_SUCCESS] Created IBAN: [{}]", response.getIban());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
