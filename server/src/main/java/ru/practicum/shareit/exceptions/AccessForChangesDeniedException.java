package ru.practicum.shareit.exceptions;

public class AccessForChangesDeniedException extends RuntimeException {
    public AccessForChangesDeniedException(final String message) {
        super(message);
    }
}
