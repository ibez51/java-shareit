package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingFilterState;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.exceptions.DateTimeValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingFilterState state = BookingFilterState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingFilterState state = BookingFilterState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getAllBookingByOwner(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable Integer bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @RequestBody @Valid BookingIncomingDto bookingIncomingDto) {

        if (bookingIncomingDto.getEnd().isBefore(LocalDateTime.now())
                || bookingIncomingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new DateTimeValidationException("Дата начала/окончания бронирования не может быть в прошлом.");
        }

        if (bookingIncomingDto.getEnd().isBefore(bookingIncomingDto.getStart())
                || bookingIncomingDto.getEnd().isEqual(bookingIncomingDto.getStart())) {
            throw new DateTimeValidationException("Дата окончания бронирования не может быть раньше или равна дате начала.");
        }

        return bookingClient.addBooking(userId, bookingIncomingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                                 @PathVariable(name = "bookingId") Integer bookingId,
                                                 @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }
}