package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ItemRequestCreateDto {
    private Integer id;
    private String description;
}
