package ru.timur.web4_back_spring.exception;

public class SessionNotFoundException extends Exception {
    public SessionNotFoundException(String message) {
        super(message);
    }
}
