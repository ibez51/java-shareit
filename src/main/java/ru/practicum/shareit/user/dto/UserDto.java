package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    private int id;
    private String name;
    @Email(message = "Поле email не соответствует формату")
    private String email;
}