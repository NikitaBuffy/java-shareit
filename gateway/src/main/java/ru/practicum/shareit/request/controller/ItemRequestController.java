package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestSort;
import ru.practicum.shareit.util.CreateValidation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated(CreateValidation.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("ItemRequestGateway: addRequest. Request: {}", itemRequestDto);
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("ItemRequestGateway: getRequests. User ID: {}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0") @Min(0) int from,
                                               @RequestParam(defaultValue = "100") @Min(1) @Max(100) int size,
                                               @RequestParam(defaultValue = "CREATED_DESC") ItemRequestSort sort) {
        log.info("ItemRequestGateway: getAllRequests. User ID: {}, from: {}, size: {}, sort: {}", userId, from, size, sort);
        return itemRequestClient.getAllRequests(userId, from, size, sort);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long requestId) {
        log.info("ItemRequestGateway: getRequests. Request ID: {}", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
