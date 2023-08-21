package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING("Новое бронирование, ожидает одобрения"),
    APPROVED("Бронирование подтверждено владельцем"),
    REJECTED("Бронирование отклонено владельцем"),
    CANCELED("Бронирование отменено создателем");

    private final String title;

    BookingStatus(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "BookingStatus{" +
                "title='" + title + '\'' +
                '}';
    }
}
