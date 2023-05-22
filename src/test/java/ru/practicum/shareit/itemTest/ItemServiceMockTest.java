package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemSort;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceMockTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private ItemDto itemDto;
    private Item item;
    private CommentDto commentDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "email@mail.ru");
        userDto = new UserDto(1L, "name", "email@mail.ru");
        itemDto = new ItemDto(1L,
                "name",
                "description",
                true,
                null,
                null,
                new ArrayList<>(),
                null);
        item = new Item(1L, "name", "description", true, user, null);
        commentDto = new CommentDto(1L, "text", null, null);
    }

    @Test
    void shouldSaveItemWhenValidWhileAddItem() {
        when(userService.getUserById(user.getId())).thenReturn(userDto);
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto addedItem = itemService.addItem(user.getId(), itemDto);

        verify(itemRepository, times(1)).save(item);
        assertEquals(ItemMapper.itemToDto(item), addedItem);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenItemOwnerNotFoundWhileAddItem() {
        when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> itemService.addItem(1L, itemDto));
        verify(itemRepository, never()).save(item);
    }

    @Test
    void shouldReturnItemWithRequestWhenItemRequestPresentsWhileAddItem() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
        itemDto.setRequestId(itemRequest.getId());
        item.setRequest(itemRequest);
        when(userService.getUserById(user.getId())).thenReturn(userDto);
        when(requestRepository.getExistingRequest(itemRequest.getId())).thenReturn(itemRequest);
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto savedItem = itemService.addItem(user.getId(), itemDto);

        verify(itemRepository, times(1)).save(item);
        assertEquals(1L, savedItem.getRequestId());
    }

    @Test
    void shouldThrowNotOwnerExceptionWhenUserNotOwnerWhileEditItem() {
        User ownerUser = new User(2L, "owner", "owner@mail.ru");
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.userToDto(ownerUser));

        assertThrows(NotOwnerException.class, () -> itemService.editItem(ownerUser.getId(), item.getId(), itemDto));
    }

    @Test
    void shouldReturnUpdatedItemWhenUserOwnerWhileEditItem() {
        ItemDto itemDataToUpdate = new ItemDto();
        itemDataToUpdate.setName("updated");
        itemDataToUpdate.setAvailable(false);
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);
        when(userService.getUserById(anyLong())).thenReturn(userDto);

        itemService.editItem(userDto.getId(), item.getId(), itemDataToUpdate);

        verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals(item.getId(), savedItem.getId());
        assertEquals("updated", savedItem.getName());
        assertEquals(false, savedItem.getAvailable());
    }

    @Test
    void shouldReturnItemWhenItemFoundWhileGetItemById() {
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);

        ItemDto actualItem = itemService.getItemById(userDto.getId(), itemDto.getId());

        assertEquals(itemDto, actualItem);
    }

    @Test
    void shouldThrowItemNotFoundExceptionWhenItemNotFoundWhileGetItemById() {
        when(itemRepository.getExistingItem(anyLong())).thenThrow(ItemNotFoundException.class);

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(item.getId(), user.getId()));
    }

    @Test
    void shouldSearchItems() {
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> items = itemService.searchItems("name", 1, 1, ItemSort.ID_DESC);

        assertEquals(1, items.size());
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextBlankWhileSearchItems() {
        List<ItemDto> items = itemService.searchItems("", 1, 1, ItemSort.ID_DESC);

        assertEquals(0, items.size());
    }

    @Test
    void shouldThrowBookingExceptionWhenNotBookedWhileAddComment() {
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);
        when(userService.getUserById(anyLong())).thenReturn(userDto);
        when(bookingRepository.findByBookerAndItemIdAndEndBeforeAndStatus(any(User.class),
                anyLong(), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(new ArrayList<>());

        assertThrows(BookingException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void shouldAddCCommentWhenBooked() {
        Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);
        when(userService.getUserById(anyLong())).thenReturn(userDto);
        when(bookingRepository.findByBookerAndItemIdAndEndBeforeAndStatus(any(User.class),
                anyLong(), any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(List.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        itemService.addComment(user.getId(), item.getId(), commentDto);

        verify(commentRepository, times(1)).save(commentArgumentCaptor.capture());
        Comment savedComment = commentArgumentCaptor.getValue();

        assertEquals(user.getName(), savedComment.getAuthor().getName());
    }

    @Test
    void shouldGetItems() {
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> items = itemService.getItems(1L, 0, 10, ItemSort.ID_DESC);

        assertEquals(1, items.size());
    }

    @Test
    void shouldReturnItemsWithBookingsWhenLastAndNextBookingsExistWhileGetItems() {
        Booking lastBooking = new Booking(1L, null, null, item, user, null);
        Booking nextBooking = new Booking(1L, null, null, item, user, null);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(lastBooking);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStart(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class))).thenReturn(nextBooking);

        List<ItemDto> items = itemService.getItems(1L, 0, 10, ItemSort.ID_DESC);

        assertEquals(1, items.size());
        assertNotNull(items.get(0).getLastBooking());
        assertNotNull(items.get(0).getNextBooking());
    }
}