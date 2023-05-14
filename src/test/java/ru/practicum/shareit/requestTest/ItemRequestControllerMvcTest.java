package ru.practicum.shareit.requestTest;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestSort;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerMvcTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    private ItemRequestDto itemRequestDto;
    private List<ItemRequestDto> items;

    @BeforeEach
    void beforeEach() {
        itemRequestDto = new ItemRequestDto(1L, "description", LocalDateTime.now(), null);

        items = List.of(itemRequestDto);
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndItemRequestWhenItemRequestValidWhileAddRequest() {
        when(itemRequestService.addRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
        verify(itemRequestService, times(1)).addRequest(anyLong(), any(ItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnBadRequestWhenItemRequestNotValidWhileAddRequest() {
        itemRequestDto.setDescription(null);
        when(itemRequestService.addRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).addRequest(anyLong(), any(ItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndRequestsWhileGetRequests() {
        when(itemRequestService.getRequests(anyLong())).thenReturn(items);

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemRequestService, times(1)).getRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndAllRequestsWhileGetAllRequests() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("from", "0");
        params.add("size", "100");
        params.add("sort", "CREATED_DESC");

        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt(),any(ItemRequestSort.class))).thenReturn(items);

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemRequestService, times(1)).getAllRequests(anyLong(), anyInt(), anyInt(), any(ItemRequestSort.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndRequestById() {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestService, times(1)).getRequestById(anyLong(), anyLong());
    }
}
