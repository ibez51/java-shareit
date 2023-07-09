package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User getUser(Integer userId);

    Integer addUser(User user);

    User updateUser(User user);

    void deleteUser(Integer userId);

    boolean isEmailInUse(String email, Integer userIdExclude);
}
