package ru.practicum.shareit.RESTTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class RESTUserTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;
    private final UserDto userDto = new UserDto().setId(1).setName("John").setEmail("john.doe@mail.com");

    @Test
    public void testGetAllUsers() throws Exception {
        doReturn(List.of(userDto))
                .when(userService)
                .getAllUsers();

        mvc.perform(get("/users")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())));
    }

    @Test
    public void testGetUser() throws Exception {
        doReturn(userDto)
                .when(userService)
                .getUserDto(anyInt());

        mvc.perform(get("/users/{id}", 1)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        doThrow(new NullPointerException())
                .when(userService)
                .getUserDto(anyInt());

        mvc.perform(get("/users/{id}", 1)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddUser() throws Exception {
        doReturn(userDto)
                .when(userService)
                .addUser(any(UserDto.class));

        mvc.perform(post("/users")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        userDto.setName("");
        mvc.perform(post("/users")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto().setName("newName").setEmail("newEmail@email.email");
        doReturn(userDto)
                .when(userService)
                .updateUser(anyInt(), any(UserUpdateDto.class));

        mvc.perform(patch("/users/{id}", 1)
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing()
                .when(userService)
                .deleteUser(anyInt());

        mvc.perform(delete("/users/{id}", 1)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void testWrongURL() throws Exception {
        mvc.perform(delete("/users/dummy", 1)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isInternalServerError());
    }
}