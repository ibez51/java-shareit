package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private int id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private int ownerId;
    private int requestId;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    @Builder.Default
    private List<CommentOutputDto> comments = new ArrayList<>();
}
