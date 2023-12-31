package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAllBooking(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                          @RequestParam(name = "state", defaultValue = "ALL") String state,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                                 @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllBookingByOwner(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                 @PathVariable(name = "bookingId") Integer bookingId) {
        return bookingService.getBookingDto(userId, bookingId);
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                 @Valid @RequestBody BookingIncomingDto bookingIncomingDto) {
        return bookingService.addBooking(userId, bookingIncomingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                     @PathVariable(name = "bookingId") Integer bookingId,
                                     @RequestParam(name = "approved") Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }
}
