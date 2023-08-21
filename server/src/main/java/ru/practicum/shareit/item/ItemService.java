package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Integer userId, Integer from, Integer size);

    ItemDto getItemDto(Integer userId, Integer itemId);

    Item getItem(Integer itemId);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    ItemDto addItem(Integer userId, ItemDto itemDto);

    CommentOutputDto addComment(Integer userId, Integer itemId, CommentCreateDto commentCreateDto);

    ItemDto updateItem(Integer itemId, Integer userId, ItemUpdateDto itemUpdateDto);

    List<Item> getItemsByRequestIds(List<Integer> itemRequestIds);
}
