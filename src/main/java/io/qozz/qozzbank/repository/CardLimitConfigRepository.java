package io.qozz.qozzbank.repository;

import io.qozz.qozzbank.domain.entity.CardLimitConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardLimitConfigRepository extends JpaRepository<CardLimitConfigEntity, UUID> {
}
