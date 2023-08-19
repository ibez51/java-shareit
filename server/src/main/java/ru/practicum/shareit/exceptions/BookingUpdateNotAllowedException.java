package ru.practicum.shareit.exceptions;

public class BookingUpdateNotAllowedException extends RuntimeException {
    public BookingUpdateNotAllowedException(final String message) {
        super(message);
    }
}
