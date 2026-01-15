package io.qozz.qozzbank.repository;

import io.qozz.qozzbank.domain.entity.CardLimitCounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardLimitCounterRepository extends JpaRepository<CardLimitCounterEntity, UUID> {
}
