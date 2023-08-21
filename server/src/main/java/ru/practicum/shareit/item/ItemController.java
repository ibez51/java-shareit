package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    @Autowired
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                           @PathVariable(name = "id") Integer itemId) {
        return itemService.getItemDto(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                           @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutputDto addComment(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                       @PathVariable(name = "itemId") Integer itemId,
                                       @RequestBody CommentCreateDto commentCreateDto) {
        return itemService.addComment(userId, itemId, commentCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable(name = "itemId") Integer itemId,
                              @RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                              @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.updateItem(itemId, userId, itemUpdateDto);
    }
}
