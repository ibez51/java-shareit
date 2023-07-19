package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Integer userId);

    ItemDto getItem(Integer userId, Integer itemId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(Integer userId, ItemDto itemDto);

    CommentOutputDto addComment(Integer userId, Integer itemId, CommentCreateDto commentCreateDto);

    ItemDto updateItem(Integer itemId, Integer userId, ItemUpdateDto itemUpdateDto);
}
