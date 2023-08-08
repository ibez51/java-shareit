package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestOutputDto> getItemRequests(Integer userId);

    ItemRequestOutputDto addItemRequest(Integer userId, ItemRequestCreateDto itemRequestCreateDto);

    List<ItemRequestOutputDto> getAllItemRequests(Integer userId, Integer from, Integer size);

    ItemRequestOutputDto getItemRequest(Integer userId, Integer itemRequestId);

    ItemRequest getItemRequest(Integer itemRequestId);
}
