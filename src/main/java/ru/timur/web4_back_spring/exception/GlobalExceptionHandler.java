package ru.timur.web4_back_spring.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public void handleUserNotFoundException(){
        log.warn("User not found");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameExistsException.class)
    public void handleUsernameExistsException(){
       log.warn("Username exists");
    }
}
