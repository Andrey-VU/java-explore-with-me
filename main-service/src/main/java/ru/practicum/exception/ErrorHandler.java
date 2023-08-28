package ru.practicum.exception;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException exception) {
        return ApiError.builder()
            .message(exception.getMessage())
            .reason("The required object was not found")
            .status(HttpStatus.NOT_FOUND)
            .timestamp(LocalDateTime.now())
            .build();
    }


    @ExceptionHandler({
    MethodArgumentTypeMismatchException.class,
    ConstraintViolationException.class,
    EwmBadDataException.class,
    MethodArgumentNotValidException.class,
    MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final Throwable e) {
        return ApiError.builder()
            .message(e.getMessage())
            .status(HttpStatus.BAD_REQUEST)
            .reason("Incorrectly made request")
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(EwmConflictException e) {
        return ApiError.builder()
            .message(e.getMessage())
            .reason("There is some conflict found")
            .status(HttpStatus.CONFLICT)
            .timestamp(LocalDateTime.now())
            .build();
    }


//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ApiError handleUnprocessedException(final Throwable e) {
//        log.debug("Получен статус 500 Internal Server Error {}, {}",  e.getMessage(), e.getStackTrace());
//        return new ApiError(
//            e.getMessage()
//        );
//    }

}