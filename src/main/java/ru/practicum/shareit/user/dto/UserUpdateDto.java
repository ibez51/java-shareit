package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;

@Data
@Accessors(chain = true)
public class UserUpdateDto {
    private String name;
    @Email(message = "Поле email не соответствует формату")
    private String email;
}
