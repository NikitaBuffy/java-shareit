package ru.practicum.shareit.requestTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @SneakyThrows
    @Test
    void shouldSerializeItemRequestDtoToJson() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description",
                LocalDateTime.of(2023, 5, 13, 15, 10, 1), null);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(LocalDateTime.of(2023, 5, 13, 15, 10, 1).toString());
    }
}