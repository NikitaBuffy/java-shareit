package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Primary
public class InMemoryUserDao implements UserDao {

    private final HashMap<Integer, User> userMap = new HashMap<>();
    int generatedId = 0;

    @Override
    public User createUser(User user) {
        checkEmail(user);
        user.setId(generateId());
        userMap.put(user.getId(), user);
        log.info("Создан пользователь с ID: {} - {}", user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(int userId) {
        if (userMap.containsKey(userId)) {
            return userMap.get(userId);
        } else {
            log.warn("Не найден пользователь при поиске по ID.");
            throw new UserNotFoundException("Пользователь с ID: " + userId + " не существует.");
        }
    }

    @Override
    public User updateUser(int userId, User user) {
        if (userMap.containsKey(userId)) {
            User mainUser = userMap.get(userId);

            if (user.getName() != null) {
                mainUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                if (!user.getEmail().equals(mainUser.getEmail())) {
                    checkEmail(user);
                }
                mainUser.setEmail(user.getEmail());
            }

            log.info("Обновлен пользователь с ID: {}. Новые данные: {}", userId, mainUser);
        } else {
            log.warn("Не найден пользователь при попытке обновления.");
            throw new UserNotFoundException("Пользователь с ID: " + userId + " не существует.");
        }
        return userMap.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Integer, User> entry : userMap.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }

    @Override
    public void deleteUser(int userId) {
        if (userMap.containsKey(userId)) {
            userMap.remove(userId);
            log.info("Удален пользователь с ID: " + userId);
        } else {
            log.warn("Не найден пользователь при попытке удаления.");
            throw new UserNotFoundException("Пользователь с ID: " + userId + " не существует.");
        }
    }

    private int generateId() {
        return ++generatedId;
    }

    private void checkEmail(User user) {
        for (Map.Entry<Integer, User> entry : userMap.entrySet()) {
            if (entry.getValue().getEmail().equals(user.getEmail())) {
                log.warn("Email: " + user.getEmail() + " уже используется другим пользователем");
                throw new ValidationException("Пользователь с таким email уже зарегистрирован!");
            }
        }
    }
}
