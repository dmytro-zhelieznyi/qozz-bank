package io.qozz.qozzbank.repository;

import io.qozz.qozzbank.domain.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepository extends JpaRepository<CardEntity, UUID> {
}
