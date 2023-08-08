package ru.practicum.shareit.JSONTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentOutputDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class JSONCommentOutputDtoTest {
    @Autowired
    private JacksonTester<CommentOutputDto> jsonCommentOutputDto;

    @Test
    void testCommentOutputDto() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        CommentOutputDto commentOutputDto = new CommentOutputDto();
        commentOutputDto.setId(1);
        commentOutputDto.setText("Text");
        commentOutputDto.setAuthorName("Author name");
        commentOutputDto.setCreated(dateTime);

        JsonContent<CommentOutputDto> result = jsonCommentOutputDto.write(commentOutputDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentOutputDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentOutputDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentOutputDto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(commentOutputDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}