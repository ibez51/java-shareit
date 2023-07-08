package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserValidationConflictException;
import ru.practicum.shareit.exceptions.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Integer userId) {
        User user = userRepository.getUser(userId);
        if (Objects.isNull(user)) {
            throw new NullPointerException("Пользователь с Id " + userId + " не найден.");
        }

        return UserMapper.userToUserDto(user);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.userDtoToUser(userDto);

        validateUserEmail(user.getEmail(), user.getId());

        Integer userId = userRepository.addUser(user);

        return UserMapper.userToUserDto(userRepository.getUser(userId));
    }

    @Override
    public UserDto updateUser(Integer userId,
                              UserDto userDto) {
        User user;

        if (Objects.nonNull(userDto.getEmail())) {
            validateUserEmail(userDto.getEmail(), userId);
        }

        user = userRepository.getUser(userId);

        if (Objects.nonNull(userDto.getName())) {
            user.setName(userDto.getName());
        }
        if (Objects.nonNull(userDto.getEmail())) {
            user.setEmail(userDto.getEmail());
        }

        return UserMapper.userToUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
    }

    private void validateUserEmail(String email, Integer userIdExclude) {
        if (Objects.isNull(email)) {
            throw new UserValidationException("Поле email должно быть заполнено");
        }

        if (userRepository.isEmailInUse(email, userIdExclude)) {
            throw new UserValidationConflictException("Пользователь с email " + email + " уже существует.");
        }
    }
}
