package io.qozz.qozzbank.service;

import io.qozz.qozzbank.domain.entity.AccountEntity;
import io.qozz.qozzbank.domain.entity.UserEntity;
import io.qozz.qozzbank.domain.enumeration.AccountStatus;
import io.qozz.qozzbank.domain.enumeration.AccountType;
import io.qozz.qozzbank.mapper.AccountMapper;
import io.qozz.qozzbank.repository.AccountRepository;
import io.qozz.qozzbank.security.context.UserContext;
import io.qozz.qozzbank.service.dto.account.AccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.AccountRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional(readOnly = true)
    public List<AccountDto> findUserAccounts() {
        UserEntity user = UserContext.getUser();
        List<AccountEntity> userAccounts = accountRepository.findByUserId(user.getId());
        return userAccounts.stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountDto findAccountByIban(String iban) {
        AccountEntity account = accountRepository.findByIban(iban).orElseThrow();
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto createAccount(AccountRequest request) {
        UserEntity user = UserContext.getUser();

        AccountEntity account = AccountEntity.builder()
                .user(user)
                .iban(generateIban(request.getCurrency()))
                .currency(request.getCurrency())
                .accountType(AccountType.fromValue(request.getAccountType().getValue()))
                .balance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .build();

        AccountEntity savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    }

    private String generateIban(String currency) {
        StringBuilder sb = new StringBuilder(currency.toUpperCase());
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
