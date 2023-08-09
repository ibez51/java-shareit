package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.CommentCreateNotAllowedException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShareItItemUnitTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    private final ItemRepository itemRepositoryMock = mock(ItemRepository.class);
    private final CommentRepository commentRepositoryMock = mock(CommentRepository.class);
    private final BookingRepository bookingRepositoryMock = mock(BookingRepository.class);
    private final UserService userServiceMock = mock(UserService.class);
    private final ItemMapper itemMapperMock = mock(ItemMapper.class);
    private final CommentMapper commentMapperMock = mock(CommentMapper.class);

    @Test
    public void testSearchItemsEmpty() {
        assertEquals(0, itemService.searchItems("", 0, 10).size());
    }

    @Test
    public void testAddCommentError() {
        itemService = new ItemServiceImpl(itemRepositoryMock, commentRepositoryMock, bookingRepositoryMock, userServiceMock, itemMapperMock, commentMapperMock);

        when(bookingRepositoryMock.existsApprovedBookingInPast(anyInt(), anyInt(), anySet(), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(CommentCreateNotAllowedException.class, () -> itemService.addComment(1, 1, null));
    }

    @Test
    public void testGetUserNotFoundError() {
        itemService = new ItemServiceImpl(itemRepositoryMock, commentRepositoryMock, bookingRepositoryMock, userServiceMock, itemMapperMock, commentMapperMock);

        doReturn(Optional.empty())
                .when(itemRepositoryMock)
                .findById(anyInt());

        assertThrows(NullPointerException.class, () -> itemService.getItem(1));
    }

    @Test
    public void testItemMapper() {
        BookingMapper bookingMapper = new BookingMapperImpl();
        CommentMapper commentMapper = new CommentMapperImpl();
        ItemMapper itemMapper = new ItemMapperImpl(bookingMapper, commentMapper);

        User user = new User().setId(1).setName("Name").setEmail("email");
        Item item = new Item().setId(1).setName("Name").setDescription("Description").setAvailable(true).setOwner(user);
        Booking lastBooking = new Booking().setId(1).setItem(item).setBooker(user);
        Booking nextBooking = new Booking().setId(2).setItem(item).setBooker(user);

        assertNull(itemMapper.toDto(null, null, null));
        assertNotNull(itemMapper.toDto(null, null, nextBooking));
        assertNotNull(itemMapper.toDto(null, lastBooking, null));
        assertNotNull(itemMapper.toDto(null, lastBooking, nextBooking));
        assertNotNull(itemMapper.toDto(item, null, null));
        assertNotNull(itemMapper.toDto(item, null, nextBooking));
        assertNotNull(itemMapper.toDto(item, lastBooking, null));
        assertNotNull(itemMapper.toDto(item, lastBooking, nextBooking));

        assertNull(itemMapper.toDto(null));
    }

    @Test
    public void testCommentMapper() {
        CommentMapper commentMapper = new CommentMapperImpl();

        User user = new User().setId(1).setName("Name").setEmail("email");
        Item item = new Item().setId(1).setName("Name").setDescription("Description").setAvailable(true).setOwner(user);
        CommentCreateDto commentCreateDto = new CommentCreateDto().setId(1).setText("Text");

        assertNull(commentMapper.toComment(null, null, null));
        assertNotNull(commentMapper.toComment(null, null, user));
        assertNotNull(commentMapper.toComment(null, item, null));
        assertNotNull(commentMapper.toComment(null, item, user));
        assertNotNull(commentMapper.toComment(commentCreateDto, null, null));
        assertNotNull(commentMapper.toComment(commentCreateDto, null, user));
        assertNotNull(commentMapper.toComment(commentCreateDto, item, null));
        assertNotNull(commentMapper.toComment(commentCreateDto, item, user));
    }
}