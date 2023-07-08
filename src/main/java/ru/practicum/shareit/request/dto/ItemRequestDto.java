package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private int id;
    private String description;
    private int requestorId;
    private LocalDateTime created;

    public static ItemRequestDto itemRequestDtoFromItemRequest(ItemRequest itemRequest) {
        return ItemRequestDto.builder().id(itemRequest.getId()).description(itemRequest.getDescription()).requestorId(itemRequest.getRequestorId()).created(itemRequest.getCreated()).build();
    }
}
