package io.qozz.qozzbank.controller.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.ErrorCode;
import org.openapitools.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handle(MissingRequestHeaderException e) {
        log.error("Missing header: ", e);
        ErrorResponse code = new ErrorResponse(ErrorCode.BAD_REQUEST_MISSING_HEADER, e.getMessage(), null);
        return ResponseEntity.badRequest().body(code);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handle(HttpMediaTypeNotSupportedException e) {
        log.error("Content type is not supported: ", e);
        ErrorResponse code = new ErrorResponse(ErrorCode.BAD_REQUEST_MEIDA_TYPE_NOT_SUPPORTED, e.getMessage(), UUID.randomUUID());
        return ResponseEntity.badRequest().body(code);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        log.error("Unsupported exception happened: ", e);
        ErrorResponse code = new ErrorResponse(null, e.getMessage(), UUID.randomUUID());
        return ResponseEntity.internalServerError().body(code);
    }
}
