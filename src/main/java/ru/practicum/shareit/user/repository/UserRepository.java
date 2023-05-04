package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmailContainingIgnoreCase(String email);

    default User getExistingUser(Long userId) {
        return findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException("Пользователь с ID: " + userId + " не существует.");
        });
    }
}
