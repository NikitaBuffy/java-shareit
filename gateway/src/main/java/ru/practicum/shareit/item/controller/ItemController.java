package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.model.ItemSort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.CreateValidation;
import ru.practicum.shareit.util.UpdateValidation;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Validated(CreateValidation.class) @RequestBody ItemDto itemDto) {
        log.info("ItemGateway: addItem. User ID: {}, item: {}", userId, itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @PathVariable Long itemId, @Validated(UpdateValidation.class) @RequestBody ItemDto itemDto) {
        log.info("ItemGateway: editItem. User ID: {}, item ID: {}", userId, itemId);
        return itemClient.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        log.info("ItemGateway: getItemById. User ID: {}, item ID: {}", userId, itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                  @RequestParam(defaultValue = "100") @Min(1) @Max(100) int size,
                                  @RequestParam(defaultValue = "ID_ASC", required = false) ItemSort sort) {
        log.info("ItemGateway: getItems. User ID: {}, from: {}, size: {}, sort: {}", userId, from, size, sort);
        return itemClient.getItems(userId, from, size, sort);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(value = "text") String text,
                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                     @RequestParam(defaultValue = "100") @Min(1) @Max(100) int size,
                                     @RequestParam(defaultValue = "ID_ASC", required = false) ItemSort sort) {
        log.info("ItemGateway: getItems. Text: {}, from: {}, size: {}, sort: {}", text, from, size, sort);
        return itemClient.searchItems(text, from, size, sort);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto) {
        log.info("ItemGateway: addComment. User ID: {}, item ID: {}, comment: {}", userId, itemId, commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
