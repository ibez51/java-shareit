package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ItemUpdateDto {
    private String name;
    private String description;
    @NotNull
    private Boolean available;
}
