package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto bookingToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.itemToItemDto(booking.getItem()))
                .booker(UserMapper.userToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking bookingIncomingDtoToBooking(BookingIncomingDto bookingIncomingDto) {
        return new Booking()
                .setStart(bookingIncomingDto.getStart())
                .setEnd(bookingIncomingDto.getEnd());
    }

    public static BookingForItemDto bookingToBookingForItemDto(Booking booking) {
        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
