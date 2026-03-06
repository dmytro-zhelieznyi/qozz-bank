package io.qozz.qozzbank.controller;

import io.qozz.qozzbank.mapper.UserMapper;
import io.qozz.qozzbank.service.UserService;
import io.qozz.qozzbank.service.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.UsersApi;
import org.openapitools.model.UserRequest;
import org.openapitools.model.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
        log.info("[USER_CREATE_START] AuthId: [{}], Email: [{}]",
                userRequest.getAuthId(), userRequest.getEmail());

        UserDto result = userService.createUser(userRequest);

        UserResponse response = userMapper.toResponse(result);

        log.info("[USER_CREATE_SUCCESS] InternalId: [{}]", result.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<UserResponse> getUserByAuthId(UUID id) {
        log.info("[USER_GET_START] AuthId: [{}]", id);

        UserDto userDto = userService.findUserByAuthId(id);

        UserResponse response = userMapper.toResponse(userDto);

        log.info("[USER_GET_SUCCESS] AuthId: [{}]", id);

        return ResponseEntity.ok(response);
    }
}