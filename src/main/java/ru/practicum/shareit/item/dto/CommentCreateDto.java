package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CommentCreateDto {
    private Integer id;
    @NotBlank
    private String text;
}
