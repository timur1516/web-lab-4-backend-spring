package ru.timur.web4_back_spring.exception;

public class InvalidAuthorizationDataException extends Exception {
    public InvalidAuthorizationDataException(String message) {
        super(message);
    }
}
