package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryJpaTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User requestor;
    private User requestor2;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        owner = userRepository.save(new User(1L, "owner", "owner@email.ru"));
        requestor = userRepository.save(new User(2L, "requestor", "requestor@email.ru"));
        requestor2 = userRepository.save(new User(3L, "requestor2", "requestor2@email.ru"));
        itemRepository.save(new Item(1L, "name", "description", true, owner, null));
        itemRequest = itemRequestRepository.save(new ItemRequest(1L, "description", requestor, LocalDateTime.now()));
        itemRequestRepository.save(new ItemRequest(2L, "description", requestor2, LocalDateTime.now()));
    }

    @Test
    void shouldReturnRequestsWhenRequestExistsByRequestorId() {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId());

        assertEquals(1, itemRequests.size());
        assertEquals("requestor", itemRequests.get(0).getRequestor().getName());
    }

    @Test
    void shouldReturnEmptyListWhenRequestNotExistsByRequestorId() {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(owner.getId());

        assertEquals(0, itemRequests.size());
    }

    @Test
    void shouldReturnAllOtherRequests() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllOtherRequests(requestor.getId(), Pageable.unpaged()).getContent();

        assertEquals(1, itemRequests.size());
        assertEquals("requestor2", itemRequests.get(0).getRequestor().getName());
    }

    @Test
    void shouldReturnRequestWhenRequestFound() {
        ItemRequest requestFound = itemRequestRepository.getExistingRequest(itemRequest.getId());

        assertNotNull(requestFound);
        assertEquals("description", requestFound.getDescription());
    }

    @Test
    void shouldThrowRequestNotFoundExceptionWhenRequestNotFound() {
        assertThrows(RequestNotFoundException.class, () -> itemRequestRepository.getExistingRequest(99L));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}