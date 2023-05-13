package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryJpaTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1L, "owner", "owner@email.ru"));
        item = itemRepository.save(new Item(1L, "Дрель", "Дрель BOSCH электрическая", true, user, null));
    }

    @Test
    void findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue_whenFound_thenReturnedItem() {
        String text = "Дрель";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text,
                Pageable.unpaged()).getContent();

        assertEquals(1, items.size());
    }

    @Test
    void findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue_whenNotFound_thenReturnedEmptyList() {
        String text = "Шуруповерт";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text,
                Pageable.unpaged()).getContent();

        assertEquals(0, items.size());
    }

    @Test
    void getExistingItem_whenItemFound_thenReturnedItem() {
        Item itemFound = itemRepository.getExistingItem(item.getId());

        assertNotNull(itemFound);
        assertEquals("Дрель", itemFound.getName());
    }

    @Test
    void getExistingItem_whenItemNotFound_thenItemNotFoundExceptionThrown() {
        assertThrows(ItemNotFoundException.class, () -> itemRepository.getExistingItem(99L));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}