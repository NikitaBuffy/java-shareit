package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    void shouldFindItemByText() {
        String text = "Дрель";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text,
                Pageable.unpaged()).getContent();

        assertEquals(1, items.size());
    }

    @Test
    void shouldReturnEmptyListWhenNotFoundWhileSearch() {
        String text = "Шуруповерт";
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text,
                Pageable.unpaged()).getContent();

        assertEquals(0, items.size());
    }

    @Test
    void shouldReturnItemWhenItemFoundWhileGetExistingItem() {
        Item itemFound = itemRepository.getExistingItem(item.getId());

        assertNotNull(itemFound);
        assertEquals("Дрель", itemFound.getName());
    }

    @Test
    void shouldThrowItemNotFoundExceptionWhenItemNotFoundWhileGetExistingItem() {
        assertThrows(ItemNotFoundException.class, () -> itemRepository.getExistingItem(99L));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}