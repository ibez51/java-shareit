package ru.practicum.shareit.JSONTests;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class JSONItemRequestOutputDtoTest {
    @Autowired
    private JacksonTester<ItemRequestOutputDto> jsonItemRequestOutputDto;

    @Test
    void testItemRequestOutputDto() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        ItemRequestOutputDto itemRequestOutputDto = new ItemRequestOutputDto()
                .setId(1)
                .setDescription("Description")
                .setCreated(dateTime)
                .setItems(Lists.newArrayList());

        JsonContent<ItemRequestOutputDto> result = jsonItemRequestOutputDto.write(itemRequestOutputDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestOutputDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestOutputDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestOutputDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(itemRequestOutputDto.getItems());
    }
}