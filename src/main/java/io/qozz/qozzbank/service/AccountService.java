package io.qozz.qozzbank.service;

import io.qozz.qozzbank.domain.entity.AccountEntity;
import io.qozz.qozzbank.domain.entity.UserEntity;
import io.qozz.qozzbank.domain.enumeration.AccountStatus;
import io.qozz.qozzbank.domain.enumeration.AccountType;
import io.qozz.qozzbank.mapper.AccountMapper;
import io.qozz.qozzbank.repository.AccountRepository;
import io.qozz.qozzbank.repository.UserRepository;
import io.qozz.qozzbank.service.dto.account.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.AccountRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public List<AccountDto> findUserAccountsById(UUID authId) {
        UserEntity user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> {
                    log.error("[SERVICE_ERROR] User profile not found for AuthId: [{}]", authId);
                    return new RuntimeException("User not found");
                });

        List<AccountEntity> userAccounts = accountRepository.findByUserId(user.getId());

        return userAccounts.stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountDto findAccountByIban(String iban) {
        AccountEntity account = accountRepository.findByIban(iban)
                .orElseThrow(() -> {
                    log.error("[SERVICE_ERROR] Account not found for iban: [{}]", iban);
                    return new RuntimeException("Account not found");
                });

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto createAccount(AccountRequest request) {
        log.info("[SERVICE] Creating new account of type [{}] for user [{}]",
                request.getAccountType(), request.getAuthId());

        UserEntity user = userRepository.findByAuthId(request.getAuthId())
                .orElseThrow(() -> {
                    log.error("[SERVICE_ERROR] User not found for AuthId: [{}]", request.getAuthId());
                    return new RuntimeException("User not found");
                });

        AccountEntity account = AccountEntity.builder()
                .user(user)
                .iban(generateIban())
                .currency(request.getCurrency())
                .accountType(AccountType.fromValue(request.getAccountType().getValue()))
                .balance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .build();

        AccountEntity savedAccount = accountRepository.save(account);
        log.info("[SERVICE] Account created successfully with IBAN: [{}]", savedAccount.getIban());

        return accountMapper.toDto(savedAccount);
    }

    private String generateIban() {
        StringBuilder sb = new StringBuilder("DE");
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
