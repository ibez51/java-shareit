package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
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
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

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
    private static final Set<String> BAD_BOOKING_STATUS_SET = Set.of(BookingStatus.REJECTED.name(), BookingStatus.CANCELED.name());

    @Override
    public List<ItemDto> getAllItems(Integer userId) {
        List<ItemDto> itemDtoList = itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
        Map<Integer, ItemDto> itemDtoMap = itemDtoList.stream()
                .collect(Collectors.toMap(ItemDto::getId, Function.identity()));

        List<Booking> lastBookingList = bookingRepository.findLastBooking(itemDtoMap.keySet(), BAD_BOOKING_STATUS_SET, LocalDateTime.now());
        List<Booking> nextBookingList = bookingRepository.findNextBooking(itemDtoMap.keySet(), BAD_BOOKING_STATUS_SET, LocalDateTime.now());

        Map<Integer, BookingForItemDto> lastBookingForItemDtoMap = lastBookingList.stream()
                .collect(Collectors.toMap(book -> book.getItem().getId(),
                        BookingMapper::bookingToBookingForItemDto));
        Map<Integer, BookingForItemDto> nextBookingForItemDtoMap = nextBookingList.stream()
                .collect(Collectors.toMap(book -> book.getItem().getId(),
                        BookingMapper::bookingToBookingForItemDto));

        itemDtoList.forEach(x -> x.setLastBooking(lastBookingForItemDtoMap.get(x.getId())));
        itemDtoList.forEach(x -> x.setNextBooking(nextBookingForItemDtoMap.get(x.getId())));

        return itemDtoList;
    }

    @Override
    public ItemDto getItem(Integer userId,
                           Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NullPointerException("Предмет с Id " + itemId + " не найден."));
        ItemDto itemDto = ItemMapper.itemToItemDto(item);

        if (userId == item.getOwnerId()) {
            Booking lastBooking = bookingRepository.findLastBooking(Set.of(itemId), BAD_BOOKING_STATUS_SET, LocalDateTime.now()).stream()
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(lastBooking)) {
                itemDto.setLastBooking(BookingMapper.bookingToBookingForItemDto(lastBooking));
            }

            Booking nextBooking = bookingRepository.findNextBooking(Set.of(itemId), BAD_BOOKING_STATUS_SET, LocalDateTime.now()).stream()
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(nextBooking)) {
                itemDto.setNextBooking(BookingMapper.bookingToBookingForItemDto(nextBooking));
            }
        }

        itemDto.setComments(commentRepository.findByItemIdOrderByCreatedAsc(itemId).stream()
                .map(CommentMapper::commentToCommentOutputDto)
                .collect(Collectors.toList()));

        return itemDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (Objects.isNull(text)
                || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findItemsByAvailabilityAndNameOrDesc(text).stream()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto addItem(Integer userId,
                           ItemDto itemDto) {
        userService.getUser(userId);

        Item item = ItemMapper.itemDtoToItem(itemDto).setOwnerId(userId);

        return ItemMapper.itemToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentOutputDto addComment(Integer userId,
                                       Integer itemId,
                                       CommentCreateDto commentCreateDto) {
        if (!bookingRepository.existsApprovedBookingInPast(itemId, userId, BAD_BOOKING_STATUS_SET, LocalDateTime.now())) {
            throw new CommentCreateNotAllowedException("Вы не можете оставить комментарий на продукт с Id " + itemId);
        }

        Comment comment = CommentMapper.commentCreateDtoToComment(commentCreateDto);
        User user = UserMapper.userDtoToUser(userService.getUser(userId));
        Item item = ItemMapper.itemDtoToItem(getItem(userId, itemId));

        comment.setItem(item).setAuthor(user);

        commentRepository.save(comment);

        return CommentMapper.commentToCommentOutputDto(comment);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Integer itemId,
                              Integer userId,
                              ItemUpdateDto itemUpdateDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NullPointerException("Предмет с Id " + itemId + " не найден."));

        if (item.getOwnerId() != userId) {
            throw new ItemOwnerConflictException("Предмет не принадлежит пользователю " + userId);
        }

        if (Objects.nonNull(itemUpdateDto.getName())) {
            item.setName(itemUpdateDto.getName());
        }
        if (Objects.nonNull(itemUpdateDto.getDescription())) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (Objects.nonNull(itemUpdateDto.getAvailable())) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }

        return ItemMapper.itemToItemDto(itemRepository.save(item));
    }
}
