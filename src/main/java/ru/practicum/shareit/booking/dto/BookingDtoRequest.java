package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.CreateValidation;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoRequest {

    private Long id;
    @FutureOrPresent(message = "Дата начала бронирования не должна быть в прошлом", groups = CreateValidation.class)
    @NotNull(message = "Необходимо указать дату начала бронирования", groups = CreateValidation.class)
    private LocalDateTime start;
    @Future(message = "Дата окончания бронировая должна быть в будущем", groups = CreateValidation.class)
    @NotNull(message = "Необходимо указать дату конца бронирования", groups = CreateValidation.class)
    private LocalDateTime end;
    private Long itemId;
}
