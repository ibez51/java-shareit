package ru.practicum.shareit.integrTests;

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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
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
class ShareItIntegrTests {
    @Autowired
    private final UserController userController;
    @Autowired
    private final ItemController itemController;
    @Autowired
    private final BookingController bookingController;
    @Autowired
    private final ItemRequestController itemRequestController;

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(itemController).isNotNull();
        assertThat(bookingController).isNotNull();
        assertThat(itemRequestController).isNotNull();
    }

    @Test
    void testAddUser() {
        UserDto userDto = new UserDto().setName("user").setEmail("user@user.com");
        UserDto newUserDto = userController.addUser(userDto);

        assertThat(newUserDto.getId()).isNotNull();

        UserDto userDtoEmptyEmail = new UserDto().setName("user");

        assertThrows(DataIntegrityViolationException.class, () -> userController.addUser(userDtoEmptyEmail));

        UserDto userDtoDuplicateEmail = new UserDto().setName("user").setEmail("user@user.com");

        assertThrows(DataIntegrityViolationException.class, () -> userController.addUser(userDtoDuplicateEmail));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto().setName("user").setEmail("user@user.com");
        userDto = userController.addUser(userDto);

        UserUpdateDto userUpdateDto = new UserUpdateDto().setName("update").setEmail("update@user.com");
        UserDto addedUserDto = userController.updateUser(userDto.getId(), userUpdateDto);

        assertEquals("update", addedUserDto.getName());
        assertEquals("update@user.com", addedUserDto.getEmail());
    }

    @Test
    void testDeleteUser() {
        UserDto userDto = new UserDto().setName("user").setEmail("user@user.com");
        userDto = userController.addUser(userDto);

        assertEquals(1, userController.getAllUsers().size());

        userController.deleteUser(userDto.getId());

        assertEquals(0, userController.getAllUsers().size());
    }

    @Test
    void testGetUser() {
        UserDto userDto = new UserDto().setName("user").setEmail("user@user.com");
        userDto = userController.addUser(userDto);

        assertEquals("user", userController.getUser(userDto.getId()).getName());
        assertThrows(NullPointerException.class, () -> userController.getUser(999));
    }

    @Test
    void testAddItem() {
        UserDto userDto = new UserDto().setName("user").setEmail("user@user.com");
        userDto = userController.addUser(userDto);

        ItemDto itemDto = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        ItemDto newItemDto = itemController.addItem(userDto.getId(), itemDto);

        assertEquals(itemDto.getName(), newItemDto.getName());
        assertThrows(NullPointerException.class, () -> itemController.addItem(999, itemDto));
    }

    @Test
    void testUpdateItem() {
        UserDto userDto = new UserDto().setName("user").setEmail("user@user.com");
        userDto = userController.addUser(userDto);

        ItemDto itemDto = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        ItemDto newItemDto = itemController.addItem(userDto.getId(), itemDto);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto().setName("Дрель+").setDescription("Аккумуляторная дрель").setAvailable(false);
        itemController.updateItem(newItemDto.getId(), userDto.getId(), itemUpdateDto);
        assertEquals(itemUpdateDto.getName(), itemController.getItem(userDto.getId(), newItemDto.getId()).getName());
        assertEquals(itemUpdateDto.getDescription(), itemController.getItem(userDto.getId(), newItemDto.getId()).getDescription());
        assertEquals(itemUpdateDto.getAvailable(), itemController.getItem(userDto.getId(), newItemDto.getId()).getAvailable());

        assertThrows(ItemOwnerConflictException.class, () -> itemController.updateItem(newItemDto.getId(), 999, itemUpdateDto));
    }

    @Test
    void testGetAllItems() {
        UserDto userDto1 = new UserDto().setName("user").setEmail("user@user.com");
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = new UserDto().setName("user").setEmail("user@user2.com");
        userDto2 = userController.addUser(userDto2);

        ItemDto itemDto1 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        itemController.addItem(userDto1.getId(), itemDto1);

        ItemDto itemDto2 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        itemController.addItem(userDto2.getId(), itemDto2);

        assertEquals(1, itemController.getAllItems(userDto1.getId(), 0, 10).size());
        assertEquals(1, itemController.getAllItems(userDto2.getId(), 0, 10).size());
    }

    @Test
    void testSearchItems() {
        UserDto userDto1 = new UserDto().setName("user").setEmail("user@user.com");
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = new UserDto().setName("user").setEmail("user@user2.com");
        userDto2 = userController.addUser(userDto2);

        ItemDto itemDto1 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        itemController.addItem(userDto1.getId(), itemDto1);

        ItemDto itemDto2 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        itemController.addItem(userDto2.getId(), itemDto2);

        ItemDto itemDto3 = new ItemDto().setName("Др1ель").setDescription("Простая др1ель").setAvailable(true);
        itemController.addItem(userDto2.getId(), itemDto3);

        assertEquals(2, itemController.searchItems("дРеЛь", 0, 10).size());
        assertEquals(0, itemController.searchItems("", 0, 10).size());

        assertEquals(3, itemController.searchItems("дР", 0, 10).size());
        userController.deleteUser(userDto1.getId());
        assertEquals(2, itemController.searchItems("дР", 0, 10).size());
    }

    @Test
    public void testComments() throws InterruptedException {
        UserDto userDto1 = userController.addUser(new UserDto()
                .setName("user1")
                .setEmail("user1@user.com"));

        UserDto userDto2 = userController.addUser(new UserDto()
                .setName("user2")
                .setEmail("user2@user.com"));

        ItemDto itemDto1 = itemController.addItem(userDto1.getId(), new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true));

        BookingIncomingDto bookingIncomingDto = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusSeconds(2))
                .setEnd(LocalDateTime.now().plusSeconds(3));
        BookingDto bookingDto = bookingController.addBooking(userDto2.getId(), bookingIncomingDto);

        CommentCreateDto commentCreateDto = new CommentCreateDto().setText("Comment for Дрель");
        assertThrows(CommentCreateNotAllowedException.class, () -> itemController.addComment(userDto2.getId(), itemDto1.getId(), commentCreateDto));

        bookingController.approveBooking(userDto1.getId(), bookingDto.getId(), true);

        Thread.sleep(9000);

        itemController.addComment(userDto2.getId(), itemDto1.getId(), commentCreateDto);

        assertEquals("Comment for Дрель", itemController.getItem(userDto1.getId(), itemDto1.getId()).getComments().get(0).getText());
    }

    @Test
    public void testAddBooking() throws InterruptedException {
        UserDto userDto1 = userController.addUser(new UserDto().setName("user1").setEmail("user1@user.com"));

        UserDto userDto2 = userController.addUser(new UserDto().setName("user2").setEmail("user2@user.com"));

        ItemDto itemDto1 = itemController.addItem(userDto1.getId(), new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true));

        BookingIncomingDto bookingIncomingDtoPast = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusSeconds(2))
                .setEnd(LocalDateTime.now().plusSeconds(3));
        BookingDto bookingDto = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoPast);

        bookingController.approveBooking(userDto1.getId(), bookingDto.getId(), true);

        Thread.sleep(9000);

        BookingIncomingDto bookingIncomingDtoFuture = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusDays(2))
                .setEnd(LocalDateTime.now().plusDays(3));
        bookingDto = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture);

        bookingController.approveBooking(userDto1.getId(), bookingDto.getId(), true);

        assertEquals(1, itemController.getItem(userDto1.getId(), itemDto1.getId()).getLastBooking().getId());
        assertEquals(2, itemController.getItem(userDto1.getId(), itemDto1.getId()).getNextBooking().getId());

        assertThrows(AccessForChangesDeniedException.class, () -> bookingController.addBooking(userDto1.getId(), bookingIncomingDtoFuture));

        BookingIncomingDto bookingIncomingDtoFail1 = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().minusDays(2))
                .setEnd(LocalDateTime.now().minusDays(1));

        assertThrows(DateTimeValidationException.class, () -> bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFail1));

        BookingIncomingDto bookingIncomingDtoFail2 = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusDays(4))
                .setEnd(LocalDateTime.now().plusDays(3));
        assertThrows(DateTimeValidationException.class, () -> bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFail2));

        itemController.updateItem(itemDto1.getId(), userDto1.getId(), new ItemUpdateDto().setAvailable(false));
        assertThrows(ItemIsUnavailableException.class, () -> bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture));

        BookingDto bookingDto1 = bookingController.getBooking(userDto1.getId(), bookingDto.getId());
        assertThrows(BookingUpdateNotAllowedException.class, () -> bookingController.approveBooking(userDto1.getId(), bookingDto1.getId(), true));
    }

    @Test
    public void testGetAllBooking() throws InterruptedException {
        UserDto userDto1 = new UserDto().setName("user1").setEmail("user1@user.com");
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = new UserDto().setName("user2").setEmail("user2@user.com");
        userDto2 = userController.addUser(userDto2);

        ItemDto itemDto1 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        itemDto1 = itemController.addItem(userDto1.getId(), itemDto1);

        BookingIncomingDto bookingIncomingDtoPast = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusSeconds(1))
                .setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoPast = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoPast);

        bookingController.approveBooking(userDto1.getId(), bookingDtoPast.getId(), false);

        Thread.sleep(3000);

        BookingIncomingDto bookingIncomingDtoCurrent = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusSeconds(2))
                .setEnd(LocalDateTime.now().plusDays(1));
        BookingDto bookingDtoCurrent = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoCurrent);

        bookingController.approveBooking(userDto1.getId(), bookingDtoCurrent.getId(), true);

        Thread.sleep(3000);

        BookingIncomingDto bookingIncomingDtoFuture = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusDays(5))
                .setEnd(LocalDateTime.now().plusDays(6));
        bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture);

        assertEquals(3, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.ALL.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.PAST.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.CURRENT.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.FUTURE.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.REJECTED.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBooking(userDto2.getId(), BookingFilterState.WAITING.name(), 0, 10).size());
    }

    @Test
    public void testGetAllBookingByOwner() throws InterruptedException {
        UserDto userDto1 = new UserDto().setName("user1").setEmail("user1@user.com");
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = userController.addUser(new UserDto().setName("user2").setEmail("user2@user.com"));

        ItemDto itemDto1 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        itemDto1 = itemController.addItem(userDto1.getId(), itemDto1);

        BookingIncomingDto bookingIncomingDtoPast = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusSeconds(1))
                .setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoPast = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoPast);

        bookingController.approveBooking(userDto1.getId(), bookingDtoPast.getId(), false);

        Thread.sleep(3000);

        BookingIncomingDto bookingIncomingDtoCurrent = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusSeconds(2))
                .setEnd(LocalDateTime.now().plusDays(1));
        BookingDto bookingDtoCurrent = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoCurrent);

        Thread.sleep(3000);

        bookingController.approveBooking(userDto1.getId(), bookingDtoCurrent.getId(), true);

        BookingIncomingDto bookingIncomingDtoFuture = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusDays(5))
                .setEnd(LocalDateTime.now().plusDays(6));
        bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture);

        assertEquals(3, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.ALL.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.PAST.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.CURRENT.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.FUTURE.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.REJECTED.name(), 0, 10).size());
        assertEquals(1, bookingController.getAllBookingByOwner(userDto1.getId(), BookingFilterState.WAITING.name(), 0, 10).size());

        assertThrows(IllegalBookingFilterStatusException.class, () -> bookingController.getAllBookingByOwner(userDto2.getId(), "ILLEGAL_VALUE", 0, 10));
    }

    @Test
    public void testGetBooking() {
        UserDto userDto1 = new UserDto().setName("user1").setEmail("user1@user.com");
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = userController.addUser(new UserDto().setName("user2").setEmail("user2@user.com"));

        ItemDto itemDto1 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        itemDto1 = itemController.addItem(userDto1.getId(), itemDto1);

        BookingIncomingDto bookingIncomingDtoPast = new BookingIncomingDto()
                .setItemId(itemDto1.getId())
                .setStart(LocalDateTime.now().plusSeconds(1))
                .setEnd(LocalDateTime.now().plusSeconds(2));
        BookingDto bookingDtoPast = bookingController.addBooking(userDto2.getId(), bookingIncomingDtoPast);

        bookingController.approveBooking(userDto1.getId(), bookingDtoPast.getId(), false);

        assertEquals(itemDto1.getId(), bookingController.getBooking(userDto2.getId(), bookingDtoPast.getId()).getItem().getId());
        assertThrows(NullPointerException.class, () -> bookingController.getBooking(userDto2.getId(), 100));
        assertThrows(NullPointerException.class, () -> bookingController.getBooking(100, bookingController.getBooking(userDto2.getId(), bookingDtoPast.getId()).getItem().getId()));
    }

    @Test
    public void testAddItemRequest() {
        UserDto userDto1 = new UserDto().setName("user1").setEmail("user1@user.com");
        userDto1 = userController.addUser(userDto1);

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto().setDescription("Item request description");
        ItemRequestOutputDto itemRequestOutputDto = itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto);

        assertEquals(1, itemRequestOutputDto.getId());
        assertEquals("Item request description", itemRequestOutputDto.getDescription());
        assertEquals(0, itemRequestOutputDto.getItems().size());

        UserDto userDto2 = userController.addUser(new UserDto().setName("user2").setEmail("user2@user.com"));

        ItemDto itemDto1 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true).setRequestId(itemRequestOutputDto.getId());
        itemController.addItem(userDto2.getId(), itemDto1);

        assertEquals(1, itemRequestController.getItemRequest(userDto1.getId(), itemRequestOutputDto.getId()).getItems().size());
    }

    @Test
    public void testGetAllItemRequests() {
        UserDto userDto1 = new UserDto().setName("user1").setEmail("user1@user.com");
        userDto1 = userController.addUser(userDto1);

        UserDto userDto2 = userController.addUser(new UserDto().setName("user2").setEmail("user2@user.com"));

        ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto1);

        ItemRequestCreateDto itemRequestCreateDto2 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto2);

        ItemRequestCreateDto itemRequestCreateDto3 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto3);

        assertEquals(3, itemRequestController.getAllItemRequests(userDto2.getId(), 0, 10).size());
    }

    @Test
    public void testGetItemRequest() {
        final UserDto userDto1 = userController.addUser(new UserDto().setName("user1").setEmail("user1@user.com"));

        ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto().setDescription("Item request description");
        ItemRequestOutputDto itemRequestOutputDto = itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto1);

        assertEquals("Item request description", itemRequestController.getItemRequest(userDto1.getId(), itemRequestOutputDto.getId()).getDescription());
        assertThrows(NullPointerException.class, () -> itemRequestController.getItemRequest(userDto1.getId(), 99));
        assertThrows(NullPointerException.class, () -> itemRequestController.getItemRequest(99, itemRequestOutputDto.getId()));
    }

    @Test
    public void testGetItemRequests() {
        UserDto userDto1 = userController.addUser(new UserDto().setName("user1").setEmail("user1@user.com"));
        UserDto userDto2 = userController.addUser(new UserDto().setName("user2").setEmail("user2@user.com"));

        ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto1);

        ItemRequestCreateDto itemRequestCreateDto2 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto2);

        ItemRequestCreateDto itemRequestCreateDto3 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto2.getId(), itemRequestCreateDto3);

        assertEquals(2, itemRequestController.getItemRequests(userDto1.getId()).size());
    }
}
