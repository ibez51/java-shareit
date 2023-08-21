package ru.practicum.shareit.exceptions;

public class CommentCreateNotAllowedException extends RuntimeException {
    public CommentCreateNotAllowedException(final String message) {
        super(message);
    }
}
