package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@DisplayName("Запрос на предмет. Unit тесты")
@ExtendWith(MockitoExtension.class)
class ShareItItemRequestUnitTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    @DisplayName("Ошибка Пользователь не найден")
    public void testGetUserNotFoundError() {
        UserService userService = mock(UserService.class);

        itemRequestService = new ItemRequestServiceImpl(mock(ItemRequestMapper.class), mock(ItemRequestRepository.class), mock(ItemService.class), userService);

        doThrow(new NullPointerException())
                .when(userService)
                .getUser(anyInt());

        assertThrows(NullPointerException.class, () -> itemRequestService.getItemRequests(1));
    }
}