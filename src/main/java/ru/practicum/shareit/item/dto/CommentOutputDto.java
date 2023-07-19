package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentOutputDto {
    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
