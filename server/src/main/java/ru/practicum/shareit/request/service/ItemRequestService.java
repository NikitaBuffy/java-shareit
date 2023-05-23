package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestSort;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, int from, int size, ItemRequestSort sort);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}
