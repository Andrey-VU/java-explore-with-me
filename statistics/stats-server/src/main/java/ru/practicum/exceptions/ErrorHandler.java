package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({EwmValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final Throwable e) {
        return ApiError.builder()
            .message(e.getMessage())
            .status(HttpStatus.BAD_REQUEST)
            .reason("Incorrectly made request")
            .timestamp(LocalDateTime.now())
            .build();
    }
}
