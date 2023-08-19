package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                                 @PathVariable(name = "id") Integer itemRequestId) {
        return itemRequestClient.getItemRequest(userId, itemRequestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Integer userId,
                                                 @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestCreateDto);
    }
}
