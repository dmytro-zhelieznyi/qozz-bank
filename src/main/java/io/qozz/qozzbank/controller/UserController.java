package io.qozz.qozzbank.controller;

import io.qozz.qozzbank.mapper.UserMapper;
import io.qozz.qozzbank.service.UserService;
import io.qozz.qozzbank.service.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.UsersApi;
import org.openapitools.model.UserData;
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
    public ResponseEntity<UserResponse> getUser(UUID xCorrelationID) {
        UserDto userDto = userService.getUser();
        UserData userData = userMapper.toUserData(userDto);
        UserResponse response = new UserResponse()
                .user(userData);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponse> createUser(UUID xCorrelationID, UserRequest userRequest) {
        UserDto userDto = userService.createUser(userRequest);
        UserData userData = userMapper.toUserData(userDto);
        UserResponse response = new UserResponse()
                .user(userData);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}