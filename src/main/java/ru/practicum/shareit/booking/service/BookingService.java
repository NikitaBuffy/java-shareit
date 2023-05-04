package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse requestBooking(Long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse confirmBooking(Long userId, Long bookingId, Boolean approved);

    BookingDtoResponse getBooking(Long userId, Long bookingId);

    List<BookingDtoResponse> getUserBookings(Long userId, String state);

    List<BookingDtoResponse> getOwnerBookings(Long userId, String state);
}
