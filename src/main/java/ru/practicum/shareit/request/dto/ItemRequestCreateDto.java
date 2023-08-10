package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class ItemRequestCreateDto {
    private Integer id;
    @NotBlank
    private String description;
}
