package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemUpdateDto {
    private String name;
    private String description;
    @NotNull
    private Boolean available;
}
