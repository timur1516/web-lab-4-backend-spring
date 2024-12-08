package ru.timur.web4_back_spring.exception;

public class RefreshTokenNotFoundException extends Exception {
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
