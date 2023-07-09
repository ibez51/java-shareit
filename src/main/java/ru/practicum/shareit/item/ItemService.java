package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Integer userId);

    ItemDto getItem(Integer itemId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer itemId, Integer userId, ItemDto itemDto);
}
