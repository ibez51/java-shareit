package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.CommentCreateNotAllowedException;
import ru.practicum.shareit.exceptions.ItemOwnerConflictException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private static final Set<String> BAD_BOOKING_STATUS_SET = Set.of(BookingStatus.REJECTED.name(), BookingStatus.CANCELED.name());

    @Override
    public List<ItemDto> getAllItems(Integer userId,
                                     Integer from,
                                     Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> itemList = itemRepository.findByOwnerIdOrderByIdAsc(userId, page).getContent();

        Map<Integer, Item> itemMap = itemList.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        List<Booking> lastBookingList = bookingRepository.findLastBooking(itemMap.keySet(), BAD_BOOKING_STATUS_SET, LocalDateTime.now());
        List<Booking> nextBookingList = bookingRepository.findNextBooking(itemMap.keySet(), BAD_BOOKING_STATUS_SET, LocalDateTime.now());

        Map<Integer, Booking> lastBookingMap = lastBookingList.stream()
                .collect(Collectors.toMap(book -> book.getItem().getId(),
                        Function.identity()));
        Map<Integer, Booking> nextBookingMap = nextBookingList.stream()
                .collect(Collectors.toMap(book -> book.getItem().getId(),
                        Function.identity()));

        return itemList.stream()
                .map(x -> itemMapper.toDto(x, lastBookingMap.get(x.getId()), nextBookingMap.get(x.getId())))
                .sorted(Comparator.comparingInt(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemDto(Integer userId,
                              Integer itemId) {
        Booking lastBooking = null;
        Booking nextBooking = null;

        Item item = getItem(itemId);

        if (userId == item.getOwner().getId()) {
            lastBooking = bookingRepository.findLastBooking(Set.of(itemId), BAD_BOOKING_STATUS_SET, LocalDateTime.now()).stream()
                    .findFirst()
                    .orElse(null);

            nextBooking = bookingRepository.findNextBooking(Set.of(itemId), BAD_BOOKING_STATUS_SET, LocalDateTime.now()).stream()
                    .findFirst()
                    .orElse(null);
        }

        return itemMapper.toDto(item,
                lastBooking,
                nextBooking,
                commentRepository.findByItemIdOrderByCreatedAsc(itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text,
                                     Integer from,
                                     Integer size) {
        if (Objects.isNull(text)
                || text.isBlank()) {
            return Collections.emptyList();
        }

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        return itemRepository.findItemsByAvailabilityAndNameOrDesc(text, page).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto addItem(Integer userId,
                           ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto, userService.getUser(userId));

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentOutputDto addComment(Integer userId,
                                       Integer itemId,
                                       CommentCreateDto commentCreateDto) {
        if (!bookingRepository.existsApprovedBookingInPast(itemId, userId, BAD_BOOKING_STATUS_SET, LocalDateTime.now())) {
            throw new CommentCreateNotAllowedException("Вы не можете оставить комментарий на продукт с Id " + itemId);
        }

        Comment comment = commentMapper.toComment(commentCreateDto,
                getItem(itemId),
                userService.getUser(userId));

        commentRepository.save(comment);

        return commentMapper.toOutputDto(comment);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Integer itemId,
                              Integer userId,
                              ItemUpdateDto itemUpdateDto) {
        Item item = getItem(itemId);

        if (item.getOwner().getId() != userId) {
            throw new ItemOwnerConflictException("Предмет не принадлежит пользователю " + userId);
        }

        itemMapper.updateItem(itemUpdateDto, item);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public Item getItem(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NullPointerException("Предмет с Id " + itemId + " не найден."));
    }

    @Override
    public List<Item> getItemsByRequestIds(List<Integer> itemRequestIds) {
        return itemRepository.findByRequestIdIn(itemRequestIds);
    }
}
