package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    User createUser(User user);

    User getUserById(Long userId);

    User updateUser(Long userId, User user);

    List<User> getAllUsers();

    void deleteUser(Long userId);
}
