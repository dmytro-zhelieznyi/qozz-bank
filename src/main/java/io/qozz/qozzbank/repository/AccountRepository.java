package io.qozz.qozzbank.repository;

import io.qozz.qozzbank.domain.entity.AccountEntity;
import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    @NonNull
    @EntityGraph(value = "Account.user")
    Optional<AccountEntity> findByIban(String iban);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(value = "Account.user")
    @Query("SELECT a FROM AccountEntity a WHERE a.iban = :iban")
    Optional<AccountEntity> findByIbanWithPessimisticLock(@Param("iban") String iban);

    List<AccountEntity> findByUserId(UUID userId);
}
