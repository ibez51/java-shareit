package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class CommentCreateDto {
    private Integer id;
    @NotBlank
    private String text;
}
