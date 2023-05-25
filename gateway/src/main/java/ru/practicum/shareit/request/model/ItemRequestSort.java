package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum ItemRequestSort {

    ID_ASC(Sort.by(Sort.Direction.ASC, "id")),
    ID_DESC(Sort.by(Sort.Direction.DESC, "id")),
    CREATED_ASC(Sort.by(Sort.Direction.ASC, "created")),
    CREATED_DESC(Sort.by(Sort.Direction.DESC, "created"));

    private final Sort sortValue;
}
