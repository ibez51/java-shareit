package ru.practicum.shareit.RESTTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.CommentCreateNotAllowedException;
import ru.practicum.shareit.exceptions.ItemOwnerConflictException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Предметы. Тесты контроллера")
@WebMvcTest(controllers = ItemController.class)
class RESTItemTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;
    private final ItemDto itemDto = new ItemDto()
            .setId(1)
            .setName("name")
            .setDescription("Item description")
            .setAvailable(true)
            .setOwnerId(1)
            .setRequestId(1);

    @Test
    @DisplayName("Список всех предметов")
    public void testGetAllItems() throws Exception {
        doReturn(List.of(itemDto))
                .when(itemService)
                .getAllItems(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking", nullValue()))
                .andExpect(jsonPath("$.[0].nextBooking", nullValue()));

        mvc.perform(get("/items")
                        .param("from", "-1")
                        .param("size", "10")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Поиск предмета по Id")
    public void testGetItem() throws Exception {
        doReturn(itemDto)
                .when(itemService)
                .getItemDto(anyInt(), anyInt());

        mvc.perform(get("/items/{id}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()));
    }

    @Test
    @DisplayName("Поиск предмета по шаблону")
    public void testSearchItems() throws Exception {
        doReturn(List.of(itemDto))
                .when(itemService)
                .searchItems(anyString(), anyInt(), anyInt());

        mvc.perform(get("/items/search")
                        .param("text", "sss")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking", nullValue()))
                .andExpect(jsonPath("$.[0].nextBooking", nullValue()));

        mvc.perform(get("/items/search")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Создание предмета")
    public void testAddItem() throws Exception {
        doReturn(itemDto)
                .when(itemService)
                .addItem(anyInt(), any(ItemDto.class));

        mvc.perform(post("/items")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()));

        itemDto.setName("");

        mvc.perform(post("/items")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Создание комментария")
    public void testAddComment() throws Exception {
        CommentOutputDto commentOutputDto = new CommentOutputDto();
        commentOutputDto.setText("Comment text");
        commentOutputDto.setAuthorName("Author");
        commentOutputDto.setCreated(LocalDateTime.of(2023, 8, 1, 5, 50));

        doReturn(commentOutputDto)
                .when(itemService)
                .addComment(anyInt(), anyInt(), any(CommentCreateDto.class));

        CommentCreateDto commentCreateDto = new CommentCreateDto().setText("CommentText");
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentOutputDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentOutputDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentOutputDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentOutputDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        doThrow(new CommentCreateNotAllowedException(""))
                .when(itemService)
                .addComment(anyInt(), anyInt(), any(CommentCreateDto.class));

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Обновление предмета")
    public void testUpdateItem() throws Exception {
        doReturn(itemDto)
                .when(itemService)
                .updateItem(anyInt(), anyInt(), any(ItemUpdateDto.class));

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto().setAvailable(false);
        mvc.perform(patch("/items/{itemId}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()));

        doThrow(new ItemOwnerConflictException(""))
                .when(itemService)
                .updateItem(anyInt(), anyInt(), any(ItemUpdateDto.class));

        mvc.perform(patch("/items/{itemId}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isForbidden());
    }
}