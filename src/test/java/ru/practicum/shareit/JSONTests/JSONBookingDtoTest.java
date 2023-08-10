package ru.practicum.shareit.JSONTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Бронирование. Тесты DTO Json")
@JsonTest
class JSONBookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Test
    @DisplayName("BookingDto")
    void testBookingDto() throws Exception {
        ItemDto itemDto = new ItemDto().setId(1);
        UserDto userDto = new UserDto().setId(2);

        BookingDto bookingDto = new BookingDto()
                .setId(1)
                .setStart(LocalDateTime.now())
                .setEnd(LocalDateTime.now())
                .setItem(itemDto)
                .setBooker(userDto)
                .setStatus(BookingStatus.APPROVED);

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingDto.getItem().getId());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingDto.getBooker().getId());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().name());
    }
}