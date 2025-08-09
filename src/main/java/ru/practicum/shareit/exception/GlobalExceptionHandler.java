package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> body(HttpStatus status, String error, String message) {
        ErrorResponse resp = new ErrorResponse(error, message, LocalDateTime.now());
        return ResponseEntity.status(status).body(resp);
    }

    // 404 — не найдено
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> notFound(NoSuchElementException ex) {
        log.warn("404 Not Found: {}", ex.getMessage());
        return body(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    // 400 — неверные данные запроса
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> badRequest(Exception ex) {
        log.warn("400 Bad Request: {}", ex.getMessage());
        return body(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    // 403 — запрет (например, редактирует не владелец)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> forbidden(SecurityException ex) {
        log.warn("403 Forbidden: {}", ex.getMessage());
        return body(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage());
    }

    // 500 — непредвиденная ошибка
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> internal(Exception ex) {
        log.error("500 Internal Server Error", ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected server error");
    }

    @ExceptionHandler(ru.practicum.shareit.exception.ConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(ru.practicum.shareit.exception.ConflictException ex) {
        log.warn("409 Conflict: {}", ex.getMessage());
        return body(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage());
    }
}