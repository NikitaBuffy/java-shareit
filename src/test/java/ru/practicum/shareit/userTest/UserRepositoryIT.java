package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository.save(new User(1L, "name", "misha023@mail.ru"));
    }

    @Test
    void findByEmailContainingIgnoreCase_whenEmailExists_thenReturnedUser() {
        List<User> users = userRepository.findByEmailContainingIgnoreCase("MiShA");

        assertEquals(1, users.size());
        assertEquals("misha023@mail.ru", users.get(0).getEmail());
    }

    @Test
    void findByEmailContainingIgnoreCase_whenEmailNotExists_thenReturnedEmptyList() {
        List<User> users = userRepository.findByEmailContainingIgnoreCase("MiShA01");

        assertEquals(0, users.size());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }
}