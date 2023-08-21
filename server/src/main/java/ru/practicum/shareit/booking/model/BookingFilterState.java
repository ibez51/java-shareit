package ru.practicum.shareit.booking.model;

public enum BookingFilterState {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    FUTURE("FUTURE"),
    WAITING("WAITING"),
    REJECTED("REJECTED");

    private final String title;

    BookingFilterState(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
