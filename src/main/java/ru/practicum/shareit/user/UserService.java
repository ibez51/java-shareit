package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserDto(Integer userId);

    User getUser(Integer userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Integer userId, UserUpdateDto userUpdateDto);

    void deleteUser(Integer userId);
}
