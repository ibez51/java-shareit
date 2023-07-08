package ru.practicum.shareit.exceptions;

public class ItemOwnerConflictException extends RuntimeException {
    public ItemOwnerConflictException(final String message) {
        super(message);
    }
}
