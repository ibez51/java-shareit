package ru.practicum.shareit.RESTTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.AccessForChangesDeniedException;
import ru.practicum.shareit.exceptions.BookingUpdateNotAllowedException;
import ru.practicum.shareit.exceptions.ItemIsUnavailableException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Бронирование. Тесты контроллера")
@WebMvcTest(controllers = BookingController.class)
class RESTBookingTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = new BookingDto()
            .setId(1)
            .setStart(LocalDateTime.of(2023, 8, 1, 5, 50))
            .setEnd(LocalDateTime.of(2023, 8, 1, 6, 50))
            .setStatus(BookingStatus.WAITING);

    @Test
    @DisplayName("Список всех бронирований")
    public void testGetAllBooking() throws Exception {
        doReturn(List.of(bookingDto))
                .when(bookingService)
                .getAllBooking(anyInt(), anyString(), anyInt(), anyInt());

        mvc.perform(get("/bookings")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @DisplayName("Список всех бронирований по вледельцу")
    public void testGetAllBookingByOwner() throws Exception {
        doReturn(List.of(bookingDto))
                .when(bookingService)
                .getAllBookingByOwner(anyInt(), anyString(), anyInt(), anyInt());

        mvc.perform(get("/bookings/owner")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.[0].start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @DisplayName("Бронирование по Id")
    public void testGetBooking() throws Exception {
        doReturn(bookingDto)
                .when(bookingService)
                .getBookingDto(anyInt(), anyInt());

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @DisplayName("Создание бронирования")
    public void testAddBooking() throws Exception {
        doReturn(bookingDto)
                .when(bookingService)
                .addBooking(anyInt(), any(BookingIncomingDto.class));

        BookingIncomingDto bookingIncomingDto = new BookingIncomingDto()
                .setItemId(1)
                .setStart(LocalDateTime.of(2023, 8, 1, 5, 50))
                .setEnd(LocalDateTime.of(2023, 8, 1, 6, 50));
        mvc.perform(post("/bookings")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingIncomingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        doThrow(new ItemIsUnavailableException(""))
                .when(bookingService)
                .addBooking(anyInt(), any(BookingIncomingDto.class));

        mvc.perform(post("/bookings")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingIncomingDto)))
                .andExpect(status().isBadRequest());

        doThrow(new AccessForChangesDeniedException(""))
                .when(bookingService)
                .addBooking(anyInt(), any(BookingIncomingDto.class));

        mvc.perform(post("/bookings")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(bookingIncomingDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Подтверждение бронирования")
    public void testApproveBooking() throws Exception {
        doReturn(bookingDto)
                .when(bookingService)
                .approveBooking(anyInt(), anyInt(), anyBoolean());

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        doThrow(new BookingUpdateNotAllowedException(""))
                .when(bookingService)
                .approveBooking(anyInt(), anyInt(), anyBoolean());

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }
}