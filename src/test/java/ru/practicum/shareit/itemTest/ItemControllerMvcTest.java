package ru.practicum.shareit.itemTest;

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
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerMvcTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    private ItemDto itemDto;
    private CommentDto commentDto;
    private List<ItemDto> items;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L,
                "name",
                "description",
                true,
                null,
                null,
                null,
                null);

        items = List.of(itemDto);

        commentDto = new CommentDto(1L, "text", null, null);
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndItemWhenItemValidWhileAddItem() {
        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1)).addItem(anyLong(), any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnBadRequestWhenItemNotValidWhileAddItem() {
        itemDto.setName(null);
        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(anyLong(), any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndEditedItem() {
        when(itemService.editItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", Matchers.is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", Matchers.is(itemDto.getAvailable()), Boolean.class));

        verify(itemService, times(1)).editItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnNotFoundWhenNotOwnerWhileEditItem() {
        when(itemService.editItem(anyLong(), anyLong(), any(ItemDto.class))).thenThrow(NotOwnerException.class);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotOwnerException));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndItemById() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        verify(itemService, times(1)).getItemById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndItems() {
        when(itemService.getItems(anyLong(), anyInt(), anyInt(), eq(null))).thenReturn(items);

        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemService, times(1)).getItems(anyLong(), anyInt(), anyInt(), eq(null));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndItemsBySearchText() {
        when(itemService.searchItems(anyString(), anyInt(), anyInt(), eq(null))).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "name")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemService, times(1)).searchItems(anyString(), anyInt(), anyInt(), eq(null));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndAddedComment() {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
        verify(itemService, times(1)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnBadRequestWhenBookingEmptyWhileAddComment() {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenThrow(BookingException.class);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingException));
    }
}