package io.qozz.qozzbank.service;

import io.qozz.qozzbank.domain.entity.UserEntity;
import io.qozz.qozzbank.domain.enumeration.UserStatus;
import io.qozz.qozzbank.mapper.UserMapper;
import io.qozz.qozzbank.repository.UserRepository;
import io.qozz.qozzbank.service.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.UserRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserRequest userRequest) {
        log.info("[SERVICE_START] Mapping user request for email: [{}], authId: [{}]",
                userRequest.getEmail(), userRequest.getAuthId());

        return userRepository.findByAuthId(userRequest.getAuthId())
                .map(userMapper::toDto)
                .orElseGet(() -> {
                    userRequest.setFirstName(userRequest.getFirstName().isBlank() ? "Anonymous" : userRequest.getFirstName());
                    userRequest.setLastName(userRequest.getLastName().isBlank() ? "Anonymous" : userRequest.getLastName());

                    UserEntity user = userMapper.toEntity(userRequest);
                    user.setStatus(UserStatus.PENDING_VERIFICATION);

                    log.info("[SERVICE_DB_SAVE] Persisting new user entity to database");
                    UserEntity savedUser = userRepository.save(user);

                    log.info("[SERVICE_SUCCESS] User profile created. InternalId: [{}], AuthId: [{}]",
                            savedUser.getId(), savedUser.getAuthId());

                    return userMapper.toDto(savedUser);
                });
    }

    @Transactional(readOnly = true)
    public UserDto findUserByAuthId(UUID authId) {
        log.info("[SERVICE_START] Looking up user profile for AuthId: [{}]", authId);

        UserEntity user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> {
                    log.error("[SERVICE_ERROR] User profile not found for AuthId: [{}]", authId);
                    return new RuntimeException("User not found");
                });

        log.info("[SERVICE_SUCCESS] User found. InternalId: [{}], Email: [{}]",
                user.getId(), user.getEmail());

        return userMapper.toDto(user);
    }
}