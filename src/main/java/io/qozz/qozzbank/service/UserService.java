package io.qozz.qozzbank.service;

import io.qozz.qozzbank.domain.entity.UserEntity;
import io.qozz.qozzbank.domain.enumeration.UserStatus;
import io.qozz.qozzbank.mapper.UserMapper;
import io.qozz.qozzbank.repository.UserRepository;
import io.qozz.qozzbank.security.context.UserContext;
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

    @Transactional(readOnly = true)
    public UserDto getUser() {
        return userMapper.toDto(UserContext.getUser());
    }

    @Transactional
    public UserDto createUser(UserRequest userRequest) {
        UUID userAuthId = UserContext.getAuthId();
        return userRepository.findByAuthId(userAuthId)
                .map(userMapper::toDto)
                .orElseGet(() -> {
                    userRequest.setFirstName(userRequest.getFirstName().isBlank() ? "Anonymous" : userRequest.getFirstName());
                    userRequest.setLastName(userRequest.getLastName().isBlank() ? "Anonymous" : userRequest.getLastName());

                    UserEntity user = userMapper.toEntity(userRequest);
                    user.setStatus(UserStatus.PENDING_VERIFICATION);

                    UserEntity savedUser = userRepository.save(user);

                    return userMapper.toDto(savedUser);
                });
    }
}
