package io.qozz.qozzbank.repository;

import io.qozz.qozzbank.domain.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findByOperationId(UUID operationId);
}
