package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryJpaTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1L, "name", "misha023@mail.ru"));
    }

    @Test
    void shouldReturnUserWhenEmailExistsWhileFindByEmail() {
        List<User> users = userRepository.findByEmailContainingIgnoreCase("MiShA");

        assertEquals(1, users.size());
        assertEquals("misha023@mail.ru", users.get(0).getEmail());
    }

    @Test
    void shouldReturnEmptyListWhenEmailNotExistsWhileFindByEmail() {
        List<User> users = userRepository.findByEmailContainingIgnoreCase("MiShA01");

        assertEquals(0, users.size());
    }

    @Test
    void shouldReturnUserWhenUserFound() {
        User userFound = userRepository.getExistingUser(user.getId());

        assertNotNull(userFound);
        assertEquals("name", userFound.getName());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userRepository.getExistingUser(99L));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }
}