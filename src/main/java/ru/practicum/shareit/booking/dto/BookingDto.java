package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder
public class BookingDto {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int itemId;
    private int bookerId;
    private BookingStatus status;

    public static BookingDto bookingDtoFromBooking(Booking booking) {
        return BookingDto.builder().id(booking.getId()).start(booking.getStart()).end(booking.getEnd()).itemId(booking.getItemId()).bookerId(booking.getBookerId()).status(booking.getStatus()).build();
    }
}
