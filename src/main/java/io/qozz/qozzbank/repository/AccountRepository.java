package io.qozz.qozzbank.repository;

import io.qozz.qozzbank.domain.entity.AccountEntity;
import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    @NonNull
    @EntityGraph(value = "Account.user")
    Optional<AccountEntity> findById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(value = "Account.user")
    @Query("SELECT a FROM AccountEntity a WHERE a.id = :id")
    Optional<AccountEntity> findByIdWithPessimisticLock(@Param("id") UUID id);
}
