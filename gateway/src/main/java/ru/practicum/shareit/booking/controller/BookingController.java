package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.BookingSort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.util.CreateValidation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> requestBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Validated(CreateValidation.class) @RequestBody BookingDtoRequest bookingDtoRequest) {
        log.info("BookingGateway: requestBooking. User ID: {}", userId);
        return bookingClient.requestBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingClient.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("BookingGateway: getBooking. Booking ID: {}, User ID: {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "100") @Min(1) @Max(100) int size,
                                              @RequestParam(defaultValue = "START_DESC") BookingSort sort) {
        BookingStatus status = BookingStatus.fromString(state);
        log.info("BookingGateway: getUserBookings. User ID: {}, from: {}, size: {}, sort: {}", userId, from, size, sort);
        return bookingClient.getUserBookings(userId, status, from, size, sort);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "100") @Min(1) @Max(100) int size,
                                              @RequestParam(defaultValue = "START_DESC") BookingSort sort) {
        BookingStatus status = BookingStatus.fromString(state);
        log.info("BookingGateway: getOwnerBookings. User ID: {}, from: {}, size: {}, sort: {}", userId, from, size, sort);
        return bookingClient.getOwnerBookings(userId, status, from, size, sort);
    }
}