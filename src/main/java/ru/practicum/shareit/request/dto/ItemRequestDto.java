package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private int id;
    private String description;
    private int requestorId;
    private LocalDateTime created;
}
