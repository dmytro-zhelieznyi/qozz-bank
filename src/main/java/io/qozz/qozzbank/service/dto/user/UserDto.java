package io.qozz.qozzbank.service.dto.user;

import io.qozz.qozzbank.domain.enumeration.UserStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        UUID authId,
        String firstName,
        String lastName,
        String email,
        UserStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

