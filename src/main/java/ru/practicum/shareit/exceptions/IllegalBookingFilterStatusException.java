package ru.practicum.shareit.exceptions;

public class IllegalBookingFilterStatusException extends RuntimeException {
    public IllegalBookingFilterStatusException(final String message) {
        super(message);
    }
}