package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDto {
    private int id;
    private String name;
    private String email;
}
