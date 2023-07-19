package ru.practicum.shareit.exceptions;

public class ItemIsUnavailableException extends RuntimeException {
    public ItemIsUnavailableException(final String message) {
        super(message);
    }
}
