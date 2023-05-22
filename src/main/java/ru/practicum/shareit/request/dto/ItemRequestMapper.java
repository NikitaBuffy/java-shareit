package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDto requestToDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );
    }

    public static ItemRequest dtoToRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                null,
                itemRequestDto.getCreated()
        );
    }
}
