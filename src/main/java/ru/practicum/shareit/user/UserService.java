package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Integer userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Integer userId, UserUpdateDto userUpdateDto);

    void deleteUser(Integer userId);
}
