package ru.timur.web4_back_spring.exception;

public class SessionTimeoutException extends Exception {
    public SessionTimeoutException(String message) {
        super(message);
    }
}
