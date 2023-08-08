package ru.practicum.shareit.RESTTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class RESTItemRequestTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    private final ItemRequestOutputDto itemRequestOutputDto = new ItemRequestOutputDto()
            .setId(1)
            .setDescription("Item request description")
            .setCreated(LocalDateTime.of(2023, 8, 1, 5, 50))
            .setItems(List.of(new ItemDto().setId(1).setDescription("Item1 description").setName("Item1 name"),
                    new ItemDto().setId(2).setDescription("Item2 description").setName("Item2 name")));

    @Test
    public void testGetItemRequests() throws Exception {
        doReturn(List.of(itemRequestOutputDto))
                .when(itemRequestService)
                .getItemRequests(anyInt());

        mvc.perform(get("/requests")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestOutputDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestOutputDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(itemRequestOutputDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].items[*]", hasSize(itemRequestOutputDto.getItems().size())));

        doThrow(new NullPointerException())
                .when(itemRequestService)
                .getItemRequests(anyInt());

        mvc.perform(get("/requests", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetItemRequest() throws Exception {
        doReturn(itemRequestOutputDto)
                .when(itemRequestService)
                .getItemRequest(anyInt(), anyInt());

        mvc.perform(get("/requests/{id}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestOutputDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestOutputDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestOutputDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items[*]", hasSize(itemRequestOutputDto.getItems().size())));
    }


    @Test
    public void testGetAllItemRequests() throws Exception {
        doReturn(List.of(itemRequestOutputDto))
                .when(itemRequestService)
                .getAllItemRequests(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestOutputDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestOutputDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(itemRequestOutputDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].items[*]", hasSize(itemRequestOutputDto.getItems().size())));

        mvc.perform(get("/requests/all")
                        .param("from", "-1")
                        .param("size", "10")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testAddItemRequests() throws Exception {
        doReturn(itemRequestOutputDto)
                .when(itemRequestService)
                .addItemRequest(anyInt(), any(ItemRequestCreateDto.class));

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();

        mvc.perform(post("/requests")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isBadRequest());

        itemRequestCreateDto.setDescription("desc");

        mvc.perform(post("/requests")
                        .accept(MediaType.ALL_VALUE)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestOutputDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestOutputDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestOutputDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.items[*]", hasSize(itemRequestOutputDto.getItems().size())));
    }
}