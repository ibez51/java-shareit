package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Email(message = "Поле email не соответствует формату")
    private String email;
}
