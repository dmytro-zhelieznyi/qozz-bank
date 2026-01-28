package io.qozz.qozzbank.repository;

import io.qozz.qozzbank.domain.entity.CardEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<CardEntity, UUID> {
    @EntityGraph(value = "Card.account")
    @NonNull
    Optional<CardEntity> findById(UUID id);
}
