package io.qozz.qozzbank.controller;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.HomeApi;
import org.openapitools.model.HomeInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the root endpoint.
 * Provides basic information about the service.
 */
@Slf4j
@RestController
public class HomeController implements HomeApi {

    @Override
    public ResponseEntity<HomeInfoResponse> getHomeInfo() {
        log.info("Info logging level: {}", "Welcome to QozzBank API");
        log.debug("Debug logging level: {}", "Welcome to QozzBank API");
        return ResponseEntity.ok(new HomeInfoResponse()
                .message("Welcome to QozzBank API"));
    }
}

