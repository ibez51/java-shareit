package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingFilterState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingFilterState> from(String stringState) {
        for (BookingFilterState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }

        return Optional.empty();
    }
}
