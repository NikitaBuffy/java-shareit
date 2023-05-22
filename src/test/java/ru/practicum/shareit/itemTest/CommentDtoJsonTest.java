package ru.practicum.shareit.itemTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @SneakyThrows
    @Test
    void shouldSerializeCommentDtoToJson() {
        CommentDto commentDto = new CommentDto(1L, "text", "John",
                LocalDateTime.of(2023, 5, 13, 15, 10, 1));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(LocalDateTime.of(2023, 5, 13, 15, 10, 1).toString());
    }
}