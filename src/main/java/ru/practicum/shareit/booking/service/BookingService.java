package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingSort;

import java.util.List;

public interface BookingService {

    BookingDtoResponse requestBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse confirmBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoResponse getBooking(Long userId, Long bookingId);

    List<BookingDtoResponse> getUserBookings(Long userId, String state, int from, int size, BookingSort sort);

    List<BookingDtoResponse> getOwnerBookings(Long userId, String state, int from, int size, BookingSort sort);
}
