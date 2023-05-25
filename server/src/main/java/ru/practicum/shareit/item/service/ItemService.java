package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemSort;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItems(Long userId, int from, int size, ItemSort sort);

    List<ItemDto> searchItems(String text, int from, int size, ItemSort sort);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
