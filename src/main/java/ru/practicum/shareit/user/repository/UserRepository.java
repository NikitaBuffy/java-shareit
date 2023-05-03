package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByEmailContainingIgnoreCase(String email);

    default User getExistingUser(int userId) {
        return findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("Пользователь с ID: " + userId + " не существует.");
        });
    }
}
