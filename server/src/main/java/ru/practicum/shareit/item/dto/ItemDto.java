package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int ownerId;
    private int requestId;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentOutputDto> comments = new ArrayList<>();
}
