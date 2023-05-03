package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse requestBooking(int userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse confirmBooking(int userId, int bookingId, Boolean approved);

    BookingDtoResponse getBooking(int userId, int bookingId);

    List<BookingDtoResponse> getUserBookings(int userId, String state);

    List<BookingDtoResponse> getOwnerBookings(int userId, String state);
}
