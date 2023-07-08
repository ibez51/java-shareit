package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemOwnerConflictException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Override
    public List<ItemDto> getAllItems(Integer userId) {
        return itemRepository.getAllItems(userId).stream()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        return ItemMapper.itemToItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (Objects.isNull(text)
                || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(Integer userId, ItemDto itemDto) {
        userService.getUser(userId);

        Item item = ItemMapper.itemDtoToItem(itemDto);
        item.setOwnerId(userId);

        return ItemMapper.itemToItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto updateItem(Integer itemId,
                              Integer userId,
                              ItemDto itemDto) {
        Item item = itemRepository.getItem(itemId);
        if (item.getOwnerId() != userId) {
            throw new ItemOwnerConflictException("Предмет не принадлежит пользователю " + userId);
        }

        if (Objects.nonNull(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if (Objects.nonNull(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (Objects.nonNull(itemDto.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.itemToItemDto(itemRepository.updateItem(item));
    }
}
