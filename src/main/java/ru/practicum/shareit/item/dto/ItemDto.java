package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.CreateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;
    @NotBlank(message = "Название предмета не должно быть пустым или содержать пробелы", groups = CreateValidation.class)
    private String name;
    @NotBlank(message = "Необходимо заполнить описание предмета", groups = CreateValidation.class)
    private String description;
    @NotNull(message = "Необходимо установить статус доступности бронирования вещи", groups = CreateValidation.class)
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;

    @Data
    public static class Booking {
        private final Long id;
        private final Long bookerId;
    }
}

