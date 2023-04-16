package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDao {

    User createUser(User user);

    User getUserById(int userId);

    User updateUser(int userId, User user);

    List<User> getAllUsers();

    void deleteUser(int userId);
}
