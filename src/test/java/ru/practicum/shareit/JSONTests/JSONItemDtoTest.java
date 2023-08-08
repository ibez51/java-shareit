package ru.practicum.shareit.JSONTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class JSONItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;

    @Test
    void testItemDto() throws Exception {
        BookingForItemDto lastBooking = new BookingForItemDto();
        lastBooking.setId(1);
        lastBooking.setBookerId(12);

        BookingForItemDto nextBooking = new BookingForItemDto();
        nextBooking.setId(2);
        nextBooking.setBookerId(12);

        CommentOutputDto commentOutputDto = new CommentOutputDto();
        commentOutputDto.setId(1);
        commentOutputDto.setText("Comment text");
        commentOutputDto.setAuthorName("Author name");
        commentOutputDto.setCreated(LocalDateTime.now());

        ItemDto itemDto = new ItemDto()
                .setId(1)
                .setName("Name")
                .setDescription("Description")
                .setAvailable(true)
                .setOwnerId(1)
                .setRequestId(1)
                .setLastBooking(lastBooking)
                .setNextBooking(nextBooking)
                .setComments(List.of(commentOutputDto));

        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(itemDto.getOwnerId());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(itemDto.getLastBooking().getId());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(itemDto.getNextBooking().getId());
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo(itemDto.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].created").isEqualTo(itemDto.getComments().get(0).getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}