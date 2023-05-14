package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestSort;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceMockTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private Item item;
    private User user;
    private UserDto userDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "email@mail.ru");
        userDto = new UserDto(1L, "name", "email@mail.ru");
        item = new Item(1L, "name", "description", true, user, null);
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
        itemRequestDto = new ItemRequestDto(1L, "description", null, null);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserNotExistsWhileAddRequest() {
        when(userRepository.getExistingUser(anyLong())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> itemRequestService.addRequest(user.getId(), itemRequestDto));
        verify(requestRepository, never()).save(itemRequest);
    }

    @Test
    void shouldAddRequestWhenUserExists() {
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto addedRequest = itemRequestService.addRequest(user.getId(), itemRequestDto);

        verify(requestRepository, times(1)).save(any(ItemRequest.class));
        assertEquals(itemRequestDto.getDescription(), addedRequest.getDescription());
    }

    @Test
    void shouldReturnRequestWithoutItemsWhenNoItemsFoundWhileGetRequest() {
        List<ItemRequest> items = List.of(itemRequest);
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(items);

        List<ItemRequestDto> itemsDto = itemRequestService.getRequests(user.getId());

        assertNotNull(itemsDto);
        assertEquals(1, itemsDto.size());
    }

    @Test
    void shouldReturnRequestWithItemsWhenItemsFoundWhileGetRequest() {
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestDto> requestsDto = itemRequestService.getRequests(user.getId());

        assertNotNull(requestsDto);
        assertEquals(1, requestsDto.size());
        assertEquals(1, requestsDto.get(0).getItems().size());
    }

    @Test
    void shouldReturnAllRequests() {
        when(requestRepository.findAllOtherRequests(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(user.getId(), 1, 1, ItemRequestSort.CREATED_DESC);

        assertEquals(1, requests.size());
    }

    @Test
    void shouldThrowRequestNotFoundExceptionWhenRequestNotFoundById() {
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(requestRepository.getExistingRequest(anyLong())).thenThrow(RequestNotFoundException.class);

        assertThrows(RequestNotFoundException.class, () -> itemRequestService.getRequestById(user.getId(), itemRequest.getId()));
    }

    @Test
    void shouldReturnRequestWhenRequestFoundById() {
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(requestRepository.getExistingRequest(anyLong())).thenReturn(itemRequest);

        ItemRequestDto actualRequest = itemRequestService.getRequestById(userDto.getId(), itemRequest.getId());

        assertEquals(itemRequestDto.getId(), actualRequest.getId());
    }
}