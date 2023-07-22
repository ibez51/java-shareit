package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingIncomingDto;
import ru.practicum.shareit.booking.model.BookingFilterState;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ShareItTests {
    @Autowired
    private final UserController userController;
    @Autowired
    private final ItemController itemController;
    @Autowired
    private final BookingController bookingController;

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(itemController).isNotNull();
        assertThat(bookingController).isNotNull();
    }

    @Test
    void testAddUser() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        UserDto newUserDto = userController.addUser(userDto);

        assertThat(newUserDto.getId()).isNotNull();

        UserDto userDtoEmptyEmail = UserDto.builder().name("user").build();

        assertThrows(DataIntegrityViolationException.class, () -> userController.addUser(userDtoEmptyEmail));

        UserDto userDtoDuplicateEmail = UserDto.builder().name("user").email("user@user.com").build();

        assertThrows(DataIntegrityViolationException.class, () -> userController.addUser(userDtoDuplicateEmail));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        userDto = userController.addUser(userDto);

        UserUpdateDto userUpdateDto = UserUpdateDto.builder().name("update").email("update@user.com").build();
        UserDto addedUserDto = userController.updateUser(userDto.getId(), userUpdateDto);

        assertEquals("update", addedUserDto.getName());
        assertEquals("update@user.com", addedUserDto.getEmail());
    }

    @Test
    void testDeleteUser() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        userDto = userController.addUser(userDto);

        assertEquals(1, userController.getAllUsers().size());

        userController.deleteUser(userDto.getId());

        assertEquals(0, userController.getAllUsers().size());
    }

    @Test
    void testGetUser() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        userDto = userController.addUser(userDto);

        assertEquals("user", userController.getUser(userDto.getId()).getName());
        assertThrows(NullPointerException.class, () -> userController.getUser(999));
    }

    @Test
    void testAddItem() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        userDto = userController.addUser(userDto);

        ItemDto itemDto = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        ItemDto newItemDto = itemController.addItem(userDto.getId(), itemDto);

        assertEquals(itemDto.getName(), newItemDto.getName());
        assertThrows(NullPointerException.class, () -> itemController.addItem(999, itemDto));
    }

    @Test
    void testUpdateItem() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        userDto = userController.addUser(userDto);

        ItemDto itemDto = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        ItemDto newItemDto = itemController.addItem(userDto.getId(), itemDto);

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder().name("Дрель+").description("Аккумуляторная дрель").available(false).build();
        itemController.updateItem(newItemDto.getId(), userDto.getId(), itemUpdateDto);
        assertEquals(itemUpdateDto.getName(), itemController.getItem(userDto.getId(), newItemDto.getId()).getName());
        assertEquals(itemUpdateDto.getDescription(), itemController.getItem(userDto.getId(), newItemDto.getId()).getDescription());
        assertEquals(itemUpdateDto.getAvailable(), itemController.getItem(userDto.getId(), newItemDto.getId()).getAvailable());

        assertThrows(ItemOwnerConflictException.class, () -> itemController.updateItem(newItemDto.getId(), 999, itemUpdateDto));
    }

    @Test
    void testGetAllItems() {
        UserDto userDto1 = UserDto.builder().name("user").email("user@user.com").build();
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = UserDto.builder().name("user").email("user@user2.com").build();
        userDto2 = userController.addUser(userDto2);

        ItemDto itemDto1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        itemController.addItem(userDto1.getId(), itemDto1);

        ItemDto itemDto2 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        itemController.addItem(userDto2.getId(), itemDto2);

        assertEquals(1, itemController.getAllItems(userDto1.getId()).size());
        assertEquals(1, itemController.getAllItems(userDto2.getId()).size());
    }

    @Test
    void testSearchItems() {
        UserDto userDto1 = UserDto.builder().name("user").email("user@user.com").build();
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = UserDto.builder().name("user").email("user@user2.com").build();
        userDto2 = userController.addUser(userDto2);

        ItemDto itemDto1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        itemController.addItem(userDto1.getId(), itemDto1);

        ItemDto itemDto2 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        itemController.addItem(userDto2.getId(), itemDto2);

        ItemDto itemDto3 = ItemDto.builder().name("Др1ель").description("Простая др1ель").available(true).build();
        itemController.addItem(userDto2.getId(), itemDto3);

        assertEquals(2, itemController.searchItems("дРеЛь").size());
        assertEquals(0, itemController.searchItems("").size());

        assertEquals(3, itemController.searchItems("дР").size());
        userController.deleteUser(userDto1.getId());
        assertEquals(2, itemController.searchItems("дР").size());
    }

    @Test
    public void testComments() throws InterruptedException {
        UserDto userDto1 = userController.addUser(UserDto.builder()
                .name("user1")
                .email("user1@user.com")
                .build());

        UserDto userDto2 = userController.addUser(UserDto.builder()
                .name("user2")
                .email("user2@user.com")
                .build());

        ItemDto itemDto1 = itemController.addItem(userDto1.getId(), ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build());

        BookingIncomingDto bookingIncomingDto = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3)).build();
        BookingDto bookingDto = bookingController.addBooking(userDto2.getId(), bookingIncomingDto);

        CommentCreateDto commentCreateDto = CommentCreateDto.builder().text("Comment for Дрель").build();
        assertThrows(CommentCreateNotAllowedException.class, () -> itemController.addComment(userDto2.getId(), itemDto1.getId(), commentCreateDto));

        bookingController.approveBooking(userDto1.getId(), bookingDto.getId(), true);

        Thread.sleep(9000);

        itemController.addComment(userDto2.getId(), itemDto1.getId(), commentCreateDto);

        assertEquals("Comment for Дрель", itemController.getItem(userDto1.getId(), itemDto1.getId()).getComments().get(0).getText());
    }

    @Test
    public void testAddBooking() throws InterruptedException {
        UserDto userDto1 = userController.addUser(UserDto.builder().name("user1").email("user1@user.com").build());

        UserDto userDto2 = userController.addUser(UserDto.builder().name("user2").email("user2@user.com").build());

        ItemDto itemDto1 = itemController.addItem(userDto1.getId(), ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build());

        BookingIncomingDto bookingIncomingDtoPast = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusSeconds(3)).build();
        BookingDto bookingDto = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoPast);

        bookingController.approveBooking(userDto1.getId(), bookingDto.getId(), true);

        Thread.sleep(9000);

        BookingIncomingDto bookingIncomingDtoFuture = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3)).build();
        bookingDto = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture);

        bookingController.approveBooking(userDto1.getId(), bookingDto.getId(), true);

        assertEquals(1, itemController.getItem(userDto1.getId(), itemDto1.getId()).getLastBooking().getId());
        assertEquals(2, itemController.getItem(userDto1.getId(), itemDto1.getId()).getNextBooking().getId());

        assertThrows(AccessForChangesDeniedException.class, () -> bookingController.addBooking(userDto1.getId(), bookingIncomingDtoFuture));

        BookingIncomingDto bookingIncomingDtoFail1 = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)).build();

        assertThrows(DateTimeValidationException.class, () -> bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFail1));

        BookingIncomingDto bookingIncomingDtoFail2 = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusDays(4))
                .end(LocalDateTime.now().plusDays(3)).build();
        assertThrows(DateTimeValidationException.class, () -> bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFail2));

        itemController.updateItem(itemDto1.getId(), userDto1.getId(), ItemUpdateDto.builder().available(false).build());
        assertThrows(ItemIsUnavailableException.class, () -> bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture));

        BookingDto bookingDto1 = bookingController.getBooking(userDto1.getId(), bookingDto.getId());
        assertThrows(BookingUpdateNotAllowedException.class, () -> bookingController.approveBooking(userDto1.getId(), bookingDto1.getId(), true));
    }

    @Test
    public void testGetAllBooking() throws InterruptedException {
        UserDto userDto1 = UserDto.builder().name("user1").email("user1@user.com").build();
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = UserDto.builder().name("user2").email("user2@user.com").build();
        userDto2 = userController.addUser(userDto2);

        ItemDto itemDto1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        itemDto1 = itemController.addItem(userDto1.getId(), itemDto1);

        BookingIncomingDto bookingIncomingDtoPast = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        BookingDto bookingDtoPast = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoPast);

        bookingController.approveBooking(userDto1.getId(), bookingDtoPast.getId(), false);

        Thread.sleep(3000);

        BookingIncomingDto bookingIncomingDtoCurrent = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusDays(1)).build();
        BookingDto bookingDtoCurrent = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoCurrent);

        bookingController.approveBooking(userDto1.getId(), bookingDtoCurrent.getId(), true);

        Thread.sleep(3000);

        BookingIncomingDto bookingIncomingDtoFuture = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6)).build();
        bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture);

        assertEquals(3, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.ALL.name()).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.PAST.name()).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.CURRENT.name()).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.FUTURE.name()).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.REJECTED.name()).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.WAITING.name()).size());
    }

    @Test
    public void testGetAllBookingByOwner() throws InterruptedException {
        UserDto userDto1 = UserDto.builder().name("user1").email("user1@user.com").build();
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = userController.addUser(UserDto.builder().name("user2").email("user2@user.com").build());

        ItemDto itemDto1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        itemDto1 = itemController.addItem(userDto1.getId(), itemDto1);

        BookingIncomingDto bookingIncomingDtoPast = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        BookingDto bookingDtoPast = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoPast);

        bookingController.approveBooking(userDto1.getId(), bookingDtoPast.getId(), false);

        Thread.sleep(3000);

        BookingIncomingDto bookingIncomingDtoCurrent = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusSeconds(2))
                .end(LocalDateTime.now().plusDays(1)).build();
        BookingDto bookingDtoCurrent = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoCurrent);

        Thread.sleep(3000);

        bookingController.approveBooking(userDto1.getId(), bookingDtoCurrent.getId(), true);

        BookingIncomingDto bookingIncomingDtoFuture = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6)).build();
        bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture);

        assertEquals(3, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.ALL.name()).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.PAST.name()).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.CURRENT.name()).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.FUTURE.name()).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.REJECTED.name()).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.WAITING.name()).size());

        assertThrows(IllegalBookingFilterStatusException.class, () -> bookingController.getAllBookingByOwner(userDto2.getId(), "ILLEGAL_VALUE"));
    }

    @Test
    public void getBooking() {
        UserDto userDto1 = UserDto.builder().name("user1").email("user1@user.com").build();
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = userController.addUser(UserDto.builder().name("user2").email("user2@user.com").build());

        ItemDto itemDto1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();
        itemDto1 = itemController.addItem(userDto1.getId(), itemDto1);

        BookingIncomingDto bookingIncomingDtoPast = BookingIncomingDto.builder()
                .itemId(itemDto1.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2)).build();
        BookingDto bookingDtoPast = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoPast);

        bookingController.approveBooking(userDto1.getId(), bookingDtoPast.getId(), false);

        assertEquals(itemDto1.getId(), bookingController.getBooking(userDto2.getId(), bookingDtoPast.getId()).getItem().getId());
        assertThrows(NullPointerException.class, () -> bookingController.getBooking(userDto2.getId(), 100));
        assertThrows(NullPointerException.class, () -> bookingController.getBooking(100, bookingController.getBooking(userDto2.getId(), bookingDtoPast.getId()).getItem().getId()));
    }
}
