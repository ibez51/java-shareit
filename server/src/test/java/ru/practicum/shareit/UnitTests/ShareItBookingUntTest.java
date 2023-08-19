package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@DisplayName("Бронирование. Unit тесты")
@ExtendWith(MockitoExtension.class)
class ShareItBookingUntTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final ItemService itemService = mock(ItemService.class);
    private final UserService userService = mock(UserService.class);
    private final BookingMapper bookingMapper = mock(BookingMapper.class);

    @Test
    @DisplayName("Список всех бронирований")
    public void testGetAllBooking() {
        bookingService = new BookingServiceImpl(bookingRepository, itemService, userService, bookingMapper);

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class));

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class));

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class));

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByBookerIdAndStatusOrderByStartDesc(anyInt(), any(BookingStatus.class), any(Pageable.class));

        bookingService.getAllBooking(1, "ALL", 0, 10);
        bookingService.getAllBooking(1, "FUTURE", 0, 10);
        bookingService.getAllBooking(1, "PAST", 0, 10);
        bookingService.getAllBooking(1, "CURRENT", 0, 10);
        bookingService.getAllBooking(1, "WAITING", 0, 10);
        bookingService.getAllBooking(1, "REJECTED", 0, 10);

        verify(bookingRepository, atMostOnce())
                .findByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class));
        verify(bookingRepository, atMostOnce())
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, atMostOnce())
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, atMostOnce())
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, atMost(2))
                .findByBookerIdAndStatusOrderByStartDesc(anyInt(), any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Список всех бронирований по владельцу")
    public void testGetAllBookingByOwner() {
        bookingService = new BookingServiceImpl(bookingRepository, itemService, userService, bookingMapper);

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByItemOwnerIdOrderByStartDesc(anyInt(), any(Pageable.class));

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class));

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class));

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));

        doReturn(Page.empty())
                .when(bookingRepository)
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(BookingStatus.class), any(Pageable.class));

        bookingService.getAllBookingByOwner(1, "ALL", 0, 10);
        bookingService.getAllBookingByOwner(1, "FUTURE", 0, 10);
        bookingService.getAllBookingByOwner(1, "PAST", 0, 10);
        bookingService.getAllBookingByOwner(1, "CURRENT", 0, 10);
        bookingService.getAllBookingByOwner(1, "WAITING", 0, 10);
        bookingService.getAllBookingByOwner(1, "REJECTED", 0, 10);

        verify(bookingRepository, atMostOnce())
                .findByItemOwnerIdOrderByStartDesc(anyInt(), any(Pageable.class));
        verify(bookingRepository, atMostOnce())
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, atMostOnce())
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, atMostOnce())
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, atMost(2))
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(BookingStatus.class), any(Pageable.class));
    }
}