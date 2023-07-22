package ru.practicum.shareit.booking;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {
    BookingDto toDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    Booking toBooking(BookingIncomingDto bookingIncomingDto, Item item, User booker, BookingStatus status);

    @Mapping(source = "booking.booker.id", target = "bookerId")
    BookingForItemDto toForItemDto(Booking booking);
}
