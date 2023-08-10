package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@DisplayName("Пользователи. Unit тесты")
@ExtendWith(MockitoExtension.class)
class ShareItUserUnitTest {
    @InjectMocks
    private UserServiceImpl userService;
    private final UserRepository userRepository = mock(UserRepository.class);

    @Test
    @DisplayName("Ошибка поиска не существующего пользователя по Id")
    public void testGetUserNotFoundError() {
        userService = new UserServiceImpl(userRepository, mock(UserMapper.class));

        doReturn(Optional.empty())
                .when(userRepository)
                .findById(anyInt());

        assertThrows(NullPointerException.class, () -> userService.updateUser(1, null));
    }
}