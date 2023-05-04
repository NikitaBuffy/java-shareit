package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(UserMapper.dtoToUser(userService.getUserById(userId)));
        Item newItem = itemRepository.save(item);
        log.info("Добавлен предмет с ID: {} - {}", newItem.getId(), newItem);
        return ItemMapper.itemToDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        Item mainItem = itemRepository.getExistingItem(itemId);
        Item itemDataToUpdate = ItemMapper.dtoToItem(itemDto);
        if (!mainItem.getOwner().equals(UserMapper.dtoToUser(userService.getUserById(userId)))) {
            log.warn("Пользователь с ID: " + userId + " не является владельцом вещи: " + mainItem);
            throw new NotOwnerException("Редактировать вещь может только её владелец!");
        }
        if (itemDataToUpdate.getName() != null) {
            mainItem.setName(itemDataToUpdate.getName());
        }
        if (itemDataToUpdate.getDescription() != null) {
            mainItem.setDescription(itemDataToUpdate.getDescription());
        }
        if (itemDataToUpdate.getAvailable() != null) {
            mainItem.setAvailable(itemDataToUpdate.getAvailable());
        }
        log.info("Обновлен предмет с ID: {}. Новые данные: {}", itemId, mainItem);
        itemRepository.save(mainItem);
        return ItemMapper.itemToDto(mainItem);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.getExistingItem(itemId);
        ItemDto itemDto = ItemMapper.itemToDto(item);

        if (userId.equals(item.getOwner().getId())) {
            findLastAndNextBookings(itemDto);
        }

        findComments(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        List<ItemDto> items = itemRepository.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        items.forEach(this::findLastAndNextBookings);
        items.forEach(this::findComments);
        return items;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text);
        return items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.getExistingItem(itemId);
        User booker = UserMapper.dtoToUser(userService.getUserById(userId));
        List<Booking> bookings = bookingRepository.findByBookerAndItemIdAndEndBeforeAndStatus(booker, itemId,
                LocalDateTime.now(), BookingStatus.APPROVED);

        if (bookings.isEmpty()) {
            log.warn("Не найдено завершенных бронирований вещи с ID: {} у пользователя с ID: {}", itemId, userId);
            throw new BookingException("Пользователь не бронировал данную вещь, либо бронирование еще не завершено");
        }

        Comment comment = CommentMapper.dtoToComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(LocalDateTime.now());
        Comment newComment = commentRepository.save(comment);
        log.info("Добавлен комментарий к вещи с ID: {} - {}", itemId, newComment.getText());
        return CommentMapper.commentToDto(newComment);
    }

    @Transactional
    private void findLastAndNextBookings(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemDto.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        if (lastBooking != null) {
            log.info("Найдено крайнее бронирование предмета с ID: {} - {}", itemDto.getId(), lastBooking);
            itemDto.setLastBooking(new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
        }

        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStart(itemDto.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        if (nextBooking != null) {
            log.info("Найдено следующее бронирование предмета с ID: {} - {}", itemDto.getId(), nextBooking);
            itemDto.setNextBooking(new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
        }
    }

    private void findComments(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findByItemId(itemDto.getId());
        itemDto.setComments(comments.stream().map(CommentMapper::commentToDto).collect(Collectors.toList()));
    }
}
