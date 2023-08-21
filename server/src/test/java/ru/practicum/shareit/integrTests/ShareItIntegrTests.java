package ru.practicum.shareit.integrTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@DisplayName("Интеграционный тест")
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
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto1;

    @BeforeEach
    void setUp() {
        userDto1 = userController.addUser(new UserDto().setName("user").setEmail("user@user.com"));
        userDto2 = userController.addUser(new UserDto().setName("user2").setEmail("user2@user.com"));
        itemDto1 = itemController.addItem(userDto1.getId(), new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true));
    }

    @Test
    @DisplayName("Проверка контекста")
    void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(itemController).isNotNull();
        assertThat(bookingController).isNotNull();
        assertThat(itemRequestController).isNotNull();
    }

    @Test
    @DisplayName("Создание пользователя")
    void testAddUser() {
        assertThat(userDto1.getId()).isNotNull();

        UserDto userDtoEmptyEmail = new UserDto().setName("user");

        assertThrows(DataIntegrityViolationException.class, () -> userController.addUser(userDtoEmptyEmail));

        UserDto userDtoDuplicateEmail = new UserDto().setName("user").setEmail("user@user.com");

        assertThrows(DataIntegrityViolationException.class, () -> userController.addUser(userDtoDuplicateEmail));
    }

    @Test
    @DisplayName("Обновление пользователя")
    void testUpdateUser() {
        UserUpdateDto userUpdateDto = new UserUpdateDto().setName("update").setEmail("update@user.com");
        UserDto addedUserDto = userController.updateUser(userDto1.getId(), userUpdateDto);

        assertEquals("update", addedUserDto.getName());
        assertEquals("update@user.com", addedUserDto.getEmail());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void testDeleteUser() {
        assertEquals(2, userController.getAllUsers().size());

        userController.deleteUser(userDto1.getId());

        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    @DisplayName("Поиск пользователя по Id")
    void testGetUser() {
        assertEquals("user", userController.getUser(userDto1.getId()).getName());
        assertThrows(NullPointerException.class, () -> userController.getUser(999));
    }

    @Test
    @DisplayName("Создание предмета")
    void testAddItem() {
        assertEquals(itemDto1.getName(), itemDto1.getName());
        assertThrows(NullPointerException.class, () -> itemController.addItem(999, itemDto1));
    }

    @Test
    @DisplayName("Обновление предмета")
    void testUpdateItem() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto().setName("Дрель+").setDescription("Аккумуляторная дрель").setAvailable(false);
        itemController.updateItem(itemDto1.getId(), userDto1.getId(), itemUpdateDto);
        assertEquals(itemUpdateDto.getName(), itemController.getItem(userDto1.getId(), itemDto1.getId()).getName());
        assertEquals(itemUpdateDto.getDescription(), itemController.getItem(userDto1.getId(), itemDto1.getId()).getDescription());
        assertEquals(itemUpdateDto.getAvailable(), itemController.getItem(userDto1.getId(), itemDto1.getId()).getAvailable());

        assertThrows(ItemOwnerConflictException.class, () -> itemController.updateItem(itemDto1.getId(), 999, itemUpdateDto));
    }

    @Test
    @DisplayName("Получение списка предметов")
    void testGetAllItems() {
        ItemDto itemDto2 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true);
        itemController.addItem(userDto2.getId(), itemDto2);

        assertEquals(1, itemController.getAllItems(userDto1.getId(), 0, 10).size());
        assertEquals(1, itemController.getAllItems(userDto2.getId(), 0, 10).size());
    }

    @Test
    @DisplayName("Поиск предмета по шаблону")
    void testSearchItems() {
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
    @DisplayName("Создание и вывод комментариев")
    public void testComments() throws InterruptedException {
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
    @DisplayName("Создание бронирования")
    public void testAddBooking() throws InterruptedException {
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

        itemController.updateItem(itemDto1.getId(), userDto1.getId(), new ItemUpdateDto().setAvailable(false));
        assertThrows(ItemIsUnavailableException.class, () -> bookingController.addBooking(userDto2.getId(), bookingIncomingDtoFuture));

        BookingDto bookingDto1 = bookingController.getBooking(userDto1.getId(), bookingDto.getId());
        assertThrows(BookingUpdateNotAllowedException.class, () -> bookingController.approveBooking(userDto1.getId(), bookingDto1.getId(), true));
    }

    @Test
    @DisplayName("Список всех бронирований")
    public void testGetAllBooking() throws InterruptedException {
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
    @DisplayName("Список всех бронирований по владельцу предмета")
    public void testGetAllBookingByOwner() throws InterruptedException {
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
    }

    @Test
    @DisplayName("Поиск бронирования по Id")
    public void testGetBooking() {
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
    @DisplayName("Создание запроса на предмет")
    public void testAddItemRequest() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto().setDescription("Item request description");
        ItemRequestOutputDto itemRequestOutputDto = itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto);

        assertEquals(1, itemRequestOutputDto.getId());
        assertEquals("Item request description", itemRequestOutputDto.getDescription());
        assertEquals(0, itemRequestOutputDto.getItems().size());

        itemDto1 = new ItemDto().setName("Дрель").setDescription("Простая дрель").setAvailable(true).setRequestId(itemRequestOutputDto.getId());
        itemController.addItem(userDto2.getId(), itemDto1);

        assertEquals(1, itemRequestController.getItemRequest(userDto1.getId(), itemRequestOutputDto.getId()).getItems().size());
    }

    @Test
    @DisplayName("Получить список запросов на предмет")
    public void testGetAllItemRequests() {
        ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto1);

        ItemRequestCreateDto itemRequestCreateDto2 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto2);

        ItemRequestCreateDto itemRequestCreateDto3 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto3);

        assertEquals(3, itemRequestController.getAllItemRequests(userDto2.getId(), 0, 10).size());
    }

    @Test
    @DisplayName("Поиск запроса на предмет по Id")
    public void testGetItemRequest() {
        ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto().setDescription("Item request description");
        ItemRequestOutputDto itemRequestOutputDto = itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto1);

        assertEquals("Item request description", itemRequestController.getItemRequest(userDto1.getId(), itemRequestOutputDto.getId()).getDescription());
        assertThrows(NullPointerException.class, () -> itemRequestController.getItemRequest(userDto1.getId(), 99));
        assertThrows(NullPointerException.class, () -> itemRequestController.getItemRequest(99, itemRequestOutputDto.getId()));
    }

    @Test
    @DisplayName("Получить список запросов на предмет")
    public void testGetItemRequests() {
        ItemRequestCreateDto itemRequestCreateDto1 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto1);

        ItemRequestCreateDto itemRequestCreateDto2 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestCreateDto2);

        ItemRequestCreateDto itemRequestCreateDto3 = new ItemRequestCreateDto().setDescription("Item request description");
        itemRequestController.addItemRequest(userDto2.getId(), itemRequestCreateDto3);

        assertEquals(2, itemRequestController.getItemRequests(userDto1.getId()).size());
    }
}
