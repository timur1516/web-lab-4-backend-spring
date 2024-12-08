package ru.timur.web4_back_spring.exception;

public class RefreshTokenTimeoutException extends Exception {
    public RefreshTokenTimeoutException(String message) {
        super(message);
    }
}
