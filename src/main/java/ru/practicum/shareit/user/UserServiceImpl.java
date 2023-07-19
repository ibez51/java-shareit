package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("Пользователь с Id " + userId + " не найден."));

        return UserMapper.userToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        return UserMapper.userToUserDto(userRepository.save(UserMapper.userDtoToUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto updateUser(Integer userId,
                              UserUpdateDto userUpdateDto) {
        User user;

        user = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("Пользователь с Id " + userId + " не найден."));

        if (Objects.nonNull(userUpdateDto.getName())) {
            user.setName(userUpdateDto.getName());
        }
        if (Objects.nonNull(userUpdateDto.getEmail())) {
            user.setEmail(userUpdateDto.getEmail());
        }

        return UserMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
}
