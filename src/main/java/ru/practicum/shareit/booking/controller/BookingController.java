package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingSort;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.CreateValidation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse requestBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated(CreateValidation.class) @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingService.requestBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse confirmBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        return bookingService.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                    @RequestParam(value = "size", defaultValue = "100") @Min(1) @Max(100) int size,
                                                    @RequestParam(value = "sort", defaultValue = "START_DESC") BookingSort sort)  {
        return bookingService.getUserBookings(userId, state, from, size, sort);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(value = "size", defaultValue = "100") @Min(1) @Max(100) int size,
                                                     @RequestParam(value = "sort", defaultValue = "START_DESC") BookingSort sort) {
        return bookingService.getOwnerBookings(userId, state, from, size, sort);
    }
}
