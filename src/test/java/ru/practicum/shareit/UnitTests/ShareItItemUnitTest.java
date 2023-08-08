package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.CommentCreateNotAllowedException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShareItItemUnitTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final UserService userService = mock(UserService.class);
    private final ItemMapper itemMapper = mock(ItemMapper.class);
    private final CommentMapper commentMapper = mock(CommentMapper.class);

    @Test
    public void testSearchItemsEmpty() {
        assertEquals(0, itemService.searchItems("", 0, 10).size());
    }

    @Test
    public void testAddCommentError() {
        itemService = new ItemServiceImpl(itemRepository, commentRepository, bookingRepository, userService, itemMapper, commentMapper);

        when(bookingRepository.existsApprovedBookingInPast(anyInt(), anyInt(), anySet(), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(CommentCreateNotAllowedException.class, () -> itemService.addComment(1, 1, null));
    }

    @Test
    public void testGetUserNotFoundError() {
        itemService = new ItemServiceImpl(itemRepository, commentRepository, bookingRepository, userService, itemMapper, commentMapper);

        doReturn(Optional.empty())
                .when(itemRepository)
                .findById(anyInt());

        assertThrows(NullPointerException.class, () -> itemService.getItem(1));
    }
}