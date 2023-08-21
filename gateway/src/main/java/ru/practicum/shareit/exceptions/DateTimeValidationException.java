package ru.practicum.shareit.exceptions;

public class DateTimeValidationException extends RuntimeException {
    public DateTimeValidationException(final String message) {
        super(message);
    }
}