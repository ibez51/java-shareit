package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestOutputDto> getItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestOutputDto getItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                               @PathVariable(name = "id") Integer itemRequestId) {
        return itemRequestService.getItemRequest(userId, itemRequestId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutputDto> getAllItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @PostMapping
    public ItemRequestOutputDto addItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                               @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.addItemRequest(userId, itemRequestCreateDto);
    }
}
