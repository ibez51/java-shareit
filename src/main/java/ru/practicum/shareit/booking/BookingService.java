package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Integer userId, BookingIncomingDto bookingIncomingDto);

    BookingDto approveBooking(Integer userId, Integer bookingId, Boolean approved);

    BookingDto getBooking(Integer userId, Integer bookingId);

    List<BookingDto> getAllBooking(Integer userId, String state);

    List<BookingDto> getAllBookingByOwner(Integer userId, String state);
}
