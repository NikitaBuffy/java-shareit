package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum BookingSort {

    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),
    START_ASC(Sort.by(Sort.Direction.ASC, "start")),
    START_DESC(Sort.by(Sort.Direction.DESC, "start")),
    END_ASC(Sort.by(Sort.Direction.ASC, "end")),
    END_DESC(Sort.by(Sort.Direction.DESC, "end"));

    private final Sort sortValue;
}
