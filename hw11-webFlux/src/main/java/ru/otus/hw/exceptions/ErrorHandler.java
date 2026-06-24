package ru.otus.hw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.RestApiErrorDto;
import ru.otus.hw.dto.RestApiValidationErrorDto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<RestApiErrorDto>> handleNotFound(EntityNotFoundException ex) {
        log.debug("Entity not found: {}", ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RestApiErrorDto("Not Found", ex.getMessage())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<RestApiValidationErrorDto>> handleInvalidArguments(WebExchangeBindException ex) {
        Map<String, String> violations = new HashMap<>();
        ex.getFieldErrors().forEach(error ->
                violations.put(error.getField(), error.getDefaultMessage())
        );
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RestApiValidationErrorDto("Validation Failed", violations)));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<RestApiErrorDto>> handleMissingParams(ServerWebInputException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RestApiErrorDto("Bad Request", ex.getReason())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<RestApiErrorDto>> handleGenericError(Exception ex) {
        log.error("Internal server error", ex);
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RestApiErrorDto("Internal Error", ex.getMessage())));
    }
}