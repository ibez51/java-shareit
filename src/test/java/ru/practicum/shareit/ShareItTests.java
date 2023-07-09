package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.ItemOwnerConflictException;
import ru.practicum.shareit.exceptions.UserValidationConflictException;
import ru.practicum.shareit.exceptions.UserValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ShareItTests {
    private final UserController userController;
    private final ItemController itemController;

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
        assertThat(itemController).isNotNull();
    }

    @Test
    void testAddUser() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        UserDto newUserDto = userController.addUser(userDto);

        assertThat(newUserDto.getId()).isNotNull();

        UserDto userDtoEmptyEmail = UserDto.builder().name("user").build();

        assertThrows(UserValidationException.class, () -> userController.addUser(userDtoEmptyEmail));

        UserDto userDtoDuplicateEmail = UserDto.builder().name("user").email("user@user.com").build();

        assertThrows(UserValidationConflictException.class, () -> userController.addUser(userDtoDuplicateEmail));
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        userDto = userController.addUser(userDto);

        UserDto updUserDto = UserDto.builder().name("update").email("update@user.com").build();
        UserDto addedUserDto = userController.updateUser(userDto.getId(), updUserDto);

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

        ItemDto updItemDto = ItemDto.builder().name("Дрель+").description("Аккумуляторная дрель").available(false).build();
        itemController.updateItem(newItemDto.getId(), userDto.getId(), updItemDto);
        assertEquals(updItemDto.getName(), itemController.getItem(newItemDto.getId()).getName());
        assertEquals(updItemDto.getDescription(), itemController.getItem(newItemDto.getId()).getDescription());
        assertEquals(updItemDto.getAvailable(), itemController.getItem(newItemDto.getId()).getAvailable());

        assertThrows(ItemOwnerConflictException.class, () -> itemController.updateItem(newItemDto.getId(), 999, updItemDto));
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
}
