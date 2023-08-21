package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDto(Integer userId) {
        return userMapper.toDto(getUser(userId));
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        return userMapper.toDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto updateUser(Integer userId,
                              UserUpdateDto userUpdateDto) {
        User user = getUser(userId);

        userMapper.updateUser(userUpdateDto, user);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        }
    }

    @Override
    public User getUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("Пользователь с Id " + userId + " не найден."));
    }
}
