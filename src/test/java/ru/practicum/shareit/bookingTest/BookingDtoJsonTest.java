package ru.practicum.shareit.bookingTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDtoResponse> json;

    @SneakyThrows
    @Test
    void shouldSerializeBookingDtoToJson() {
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(1L,
                LocalDateTime.of(2023, 5, 13, 15, 10, 1),
                LocalDateTime.of(2023, 5, 14, 15, 10, 1),
                null,
                null,
                BookingStatus.WAITING);

        JsonContent<BookingDtoResponse> result = json.write(bookingDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(LocalDateTime.of(2023, 5, 13, 15, 10, 1).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2023, 5, 14, 15, 10, 1).toString());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }
}