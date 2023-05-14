package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerMvcTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(1L, "name", "email@mail.ru");
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndUsersWhileGetAllUsers() {
        List<UserDto> userList = List.of(userDto);
        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndUserWhileGetUserById() {
        when(userService.getUserById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        verify(userService, times(1)).getUserById(anyLong());
    }

    @SneakyThrows
    @Test
    void shouldReturnNotFoundWhenUserNotFoundWhileGetUserById() {
        when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndCreateUserWhenUserValid() {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnBadRequestWhenUserNotValidWhileCreateUser() {
        userDto.setName(null);
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

        verify(userService, never()).createUser(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndUserWhileUpdateUser() {
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));

        verify(userService, times(1)).updateUser(anyLong(), any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkWhileDeleteUser() {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyLong());
    }
}