package ru.practicum.shareit.bookingTest;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingSort;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerMvcTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    private BookingDtoResponse bookingDtoResponse;
    private BookingDtoRequest bookingDtoRequest;
    private List<BookingDtoResponse> bookings;

    @BeforeEach
    void beforeEach() {
        bookingDtoRequest = new BookingDtoRequest(1L,
                LocalDateTime.of(2023, 5, 25, 15, 0),
                LocalDateTime.of(2023, 5, 26, 15, 0),
                1L);

        bookingDtoResponse = new BookingDtoResponse(1L,
                LocalDateTime.of(2023, 5, 25, 15, 0),
                LocalDateTime.of(2023, 5, 26, 15, 0),
                new BookingDtoResponse.Item(1L, "itemName"),
                new BookingDtoResponse.User(1L, "name"),
                null);

        bookings = List.of(bookingDtoResponse);
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndBookingWhenBookingValidWhileRequestBooking() {
        when(bookingService.requestBooking(anyLong(), any(BookingDtoRequest.class))).thenReturn(bookingDtoResponse);

        String result = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoResponse), result);
        verify(bookingService, times(1)).requestBooking(anyLong(), any(BookingDtoRequest.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnBadRequestWhenBookingNotValidWhileRequestBooking() {
        bookingDtoRequest.setStart(LocalDateTime.of(2020, 10, 10, 10, 0));
        when(bookingService.requestBooking(anyLong(), any(BookingDtoRequest.class))).thenReturn(bookingDtoResponse);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).requestBooking(anyLong(), any(BookingDtoRequest.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndBookingWhileConfirmBooking() {
        when(bookingService.confirmBooking(anyLong(), anyLong(), any(Boolean.class))).thenReturn(bookingDtoResponse);

        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoResponse)));

        verify(bookingService, times(1)).confirmBooking(anyLong(), anyLong(), any(Boolean.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndBookingWhileGetBooking() {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDtoResponse);

        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoResponse)));

        verify(bookingService, times(1)).getBooking(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndBookingWhileGetUserBookings() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("state", "ALL");
        params.add("from", "0");
        params.add("size", "100");
        params.add("sort", "START_DESC");
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt(), any(BookingSort.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        verify(bookingService, times(1)).getUserBookings(anyLong(), anyString(), anyInt(), anyInt(), any(BookingSort.class));
    }

    @SneakyThrows
    @Test
    void shouldReturnOkAndBookingWhileGetOwnerBookings() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("state", "ALL");
        params.add("from", "0");
        params.add("size", "100");
        params.add("sort", "START_DESC");
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt(), any(BookingSort.class))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(bookings)));

        verify(bookingService, times(1)).getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt(), any(BookingSort.class));
    }
}
