package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.CreateValidation;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse requestBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @Validated(CreateValidation.class) @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingService.requestBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse confirmBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable int bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        return bookingService.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @PathVariable int bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getUserBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") int userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(userId, state);
    }
}
