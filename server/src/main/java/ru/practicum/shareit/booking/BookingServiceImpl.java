package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilterState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.AccessForChangesDeniedException;
import ru.practicum.shareit.exceptions.BookingUpdateNotAllowedException;
import ru.practicum.shareit.exceptions.ItemIsUnavailableException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto getBookingDto(Integer userId,
                                    Integer bookingId) {
        Booking booking = getBooking(userId, bookingId);

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllBooking(Integer userId,
                                          String state,
                                          Integer from,
                                          Integer size) {
        List<Booking> bookingList;

        userService.getUser(userId);

        BookingFilterState filterState = BookingFilterState.valueOf(state);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (filterState) {
            case ALL:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId, page).getContent();
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), page).getContent();
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), page).getContent();
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now(), LocalDateTime.now(), page).getContent();
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page).getContent();
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page).getContent();
                break;
            default:
                return null;
        }

        return bookingList.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingByOwner(Integer userId,
                                                 String state,
                                                 Integer from,
                                                 Integer size) {
        List<Booking> bookingList;

        userService.getUser(userId);

        BookingFilterState filterState = BookingFilterState.valueOf(state);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (filterState) {
            case ALL:
                bookingList = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, page).getContent();
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), page).getContent();
                break;
            case PAST:
                bookingList = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), page).getContent();
                break;
            case CURRENT:
                bookingList = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now(), LocalDateTime.now(), page).getContent();
                break;
            case WAITING:
                bookingList = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, page).getContent();
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, page).getContent();
                break;
            default:
                return null;
        }

        return bookingList.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto addBooking(Integer userId,
                                 BookingIncomingDto bookingIncomingDto) {
        Item item = itemService.getItem(bookingIncomingDto.getItemId());

        if (item.getOwner().getId() == userId) {
            throw new AccessForChangesDeniedException("Вы не можете забронировать свой же предмет");
        }

        if (!item.getAvailable()) {
            throw new ItemIsUnavailableException("Предмет с Id " + bookingIncomingDto.getItemId() + " недоступен для бронирования.");
        }

        User user = userService.getUser(userId);

        Booking booking = bookingMapper.toBooking(bookingIncomingDto, item, user, BookingStatus.WAITING);

        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Integer userId,
                                     Integer bookingId,
                                     Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NullPointerException("Бронь с Id " + bookingId + " не найдена."));

        Item item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            throw new AccessForChangesDeniedException("Доступ к бронированию запрещен.");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingUpdateNotAllowedException("Бронирование с Id " + bookingId + " уже подтверждено.");
        }

        booking.setStatus(approved ?
                BookingStatus.APPROVED :
                BookingStatus.REJECTED);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public Booking getBooking(Integer userId,
                              Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NullPointerException("Бронирование с Id " + bookingId + " не найдено."));

        if (booking.getBooker().getId() != userId
                && booking.getItem().getOwner().getId() != userId) {
            throw new NullPointerException("Бронирование с Id " + bookingId + " не найдено.");
        }

        return booking;
    }
}
