package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    private int id;
    private String description;
    private int requestorId;
    private LocalDateTime created;
}