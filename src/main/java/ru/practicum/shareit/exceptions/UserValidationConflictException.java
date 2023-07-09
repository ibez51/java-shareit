package ru.practicum.shareit.exceptions;

public class UserValidationConflictException extends RuntimeException {
    public UserValidationConflictException(final String message) {
        super(message);
    }
}
