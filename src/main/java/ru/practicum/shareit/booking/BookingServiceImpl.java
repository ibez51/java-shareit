package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilterState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
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

    @Override
    public BookingDto getBooking(Integer userId,
                                 Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NullPointerException("Бронирование с Id " + bookingId + " не найдено."));

        if (booking.getBooker().getId() != userId
                && booking.getItem().getOwnerId() != userId) {
            throw new NullPointerException("Бронирование с Id " + bookingId + " не найдено.");
        }

        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBooking(Integer userId,
                                          String state) {
        List<Booking> bookingList;

        userService.getUser(userId);

        BookingFilterState filterState = bookingFilterStateFromString(state);

        switch (filterState) {
            case ALL:
                bookingList = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case PAST:
                bookingList = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingList = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                return null;
        }

        return bookingList.stream()
                .map(BookingMapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingByOwner(Integer userId,
                                                 String state) {
        List<Booking> bookingList;

        userService.getUser(userId);

        BookingFilterState filterState = bookingFilterStateFromString(state);

        switch (filterState) {
            case ALL:
                bookingList = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case FUTURE:
                bookingList = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case PAST:
                bookingList = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingList = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                return null;
        }

        return bookingList.stream()
                .map(BookingMapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto addBooking(Integer userId,
                                 BookingIncomingDto bookingIncomingDto) {
        Item item = ItemMapper.itemDtoToItem(itemService.getItem(userId, bookingIncomingDto.getItemId()));

        if (item.getOwnerId() == userId) {
            throw new AccessForChangesDeniedException("Вы не можете забронировать свой же предмет");
        }

        if (!item.getAvailable()) {
            throw new ItemIsUnavailableException("Предмет с Id " + bookingIncomingDto.getItemId() + " недоступен для бронирования.");
        }

        User user = UserMapper.userDtoToUser(userService.getUser(userId));

        if (bookingIncomingDto.getEnd().isBefore(LocalDateTime.now())
                || bookingIncomingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new DateTimeValidationException("Дата начала/окончания бронирования не может быть в прошлом.");
        }

        if (bookingIncomingDto.getEnd().isBefore(bookingIncomingDto.getStart())
                || bookingIncomingDto.getEnd().isEqual(bookingIncomingDto.getStart())) {
            throw new DateTimeValidationException("Дата окончания бронирования не может быть раньше или равна дате начала.");
        }

        Booking booking = BookingMapper.bookingIncomingDtoToBooking(bookingIncomingDto)
                .setItem(item)
                .setBooker(user)
                .setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking);

        return BookingMapper.bookingToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Integer userId,
                                     Integer bookingId,
                                     Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NullPointerException("Бронь с Id " + bookingId + " не найдена."));

        Item item = booking.getItem();
        if (item.getOwnerId() != userId) {
            throw new AccessForChangesDeniedException("Доступ к бронированию запрещен.");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingUpdateNotAllowedException("Бронирование с Id " + bookingId + " уже подтверждено.");
        }

        booking.setStatus(approved ?
                BookingStatus.APPROVED :
                BookingStatus.REJECTED);

        return BookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

    private BookingFilterState bookingFilterStateFromString(String state) {
        try {
            return BookingFilterState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new IllegalBookingFilterStatusException("Unknown state: " + state);
        }
    }
}
