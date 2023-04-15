package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(int userId);

    UserDto updateUser(int userId, UserDto userDto);

    List<UserDto> getAllUsers();

    void deleteUser(int userId);
}
