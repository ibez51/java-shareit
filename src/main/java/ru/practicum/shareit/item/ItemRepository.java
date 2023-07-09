package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllItems(Integer userId);

    Item getItem(Integer itemId);

    List<Item> searchItems(String text);

    Item addItem(Item item);

    Item updateItem(Item item);

    void deleteItemsByUser(Integer userId);
}
