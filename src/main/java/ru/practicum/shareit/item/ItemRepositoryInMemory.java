package ru.practicum.shareit.item;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Integer, Item> itemsMap = new HashMap<>();
    private Integer itemIdNumberSeq = 0;

    @Override
    public List<Item> getAllItems(Integer userId) {
        return itemsMap.values().stream()
                .filter(x -> x.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItem(Integer itemId) {
        return itemsMap.get(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemsMap.values().stream()
                .filter(Item::getAvailable)
                .filter(x -> x.getName().toLowerCase().contains(text.toLowerCase())
                        || x.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Item addItem(Item item) {
        item.setId(getId());
        itemsMap.put(item.getId(), item);

        return item;
    }

    @Override
    public Item updateItem(Item item) {
        return itemsMap.put(item.getId(), item);
    }

    @Override
    public void deleteItemsByUser(Integer userId) {
        List<Integer> itemsListToDelete = itemsMap.values().stream()
                .filter(x -> x.getOwnerId() == userId)
                .map(Item::getId)
                .collect(Collectors.toList());

        itemsListToDelete.forEach(itemsMap.keySet()::remove);
    }

    private Integer getId() {
        return ++itemIdNumberSeq;
    }
}
