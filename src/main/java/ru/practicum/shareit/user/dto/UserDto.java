package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.CreateValidation;
import ru.practicum.shareit.util.UpdateValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int id;
    @NotBlank(message = "Имя пользователя не должно содержать пробелы или быть пустым", groups = CreateValidation.class)
    private String name;
    @NotBlank(message = "Email не должен содержать пробелы или быть пустым", groups = CreateValidation.class)
    @Email(message = "Неверный email. Убедитесь, что формат соответствует email",
            groups = {CreateValidation.class, UpdateValidation.class})
    private String email;
}

