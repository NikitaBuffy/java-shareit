package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoResponse requestBooking(int userId, BookingDtoRequest bookingDtoRequest) {
        Booking booking = BookingMapper.requestDtoToBooking(bookingDtoRequest);
        booking.setBooker(userRepository.getExistingUser(userId));
        booking.setItem(itemRepository.getExistingItem(bookingDtoRequest.getItemId()));
        requestValidation(booking);
        bookingRepository.save(booking);
        log.info("Оставлен запрос от пользователя с ID: {} на вещь {}", userId, booking.getItem());
        return BookingMapper.bookingToDtoResponse(booking);
    }

    @Override
    @Transactional
    public BookingDtoResponse confirmBooking(int userId, int bookingId, Boolean approved) {
        Booking booking = bookingRepository.getExistingBooking(bookingId);
        Item item = booking.getItem();

        if (!item.getOwner().equals(userRepository.getExistingUser(userId))) {
            log.warn("Пользователь с ID: " + userId + " не является владельцом вещи: " + item);
            throw new NotOwnerException("Пользователь не является владельцом вещи");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.warn("Бронирование с ID: {} уже подтверждено владельцем с ID {}", bookingId, userId);
            throw new AlreadyApprovedException("Владелец уже подтвердил данное бронирование");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.info("Бронирование с ID: {} подтверждено владельцем с ID: {}", bookingId, userId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.info("Бронирование с ID: {} отклонено владельцем с ID: {}", bookingId, userId);
        }

        return BookingMapper.bookingToDtoResponse(booking);
    }

    @Override
    @Transactional
    public BookingDtoResponse getBooking(int userId, int bookingId) {
        Booking booking = bookingRepository.getExistingBooking(bookingId);
        User user = userRepository.getExistingUser(userId);

        if (!booking.getBooker().equals(user) && !booking.getItem().getOwner().equals(user)) {
            log.warn("Пользователь с ID: " + userId + " не имеет отношение к " + booking);
            throw new NotOwnerException("Получить данные о бронировании может либо владелец, либо автор бронирования");
        }

        return BookingMapper.bookingToDtoResponse(booking);
    }

    @Override
    @Transactional
    public List<BookingDtoResponse> getUserBookings(int userId, String state) {
        User booker = userRepository.getExistingUser(userId);
        BookingStatus status = BookingStatus.fromString(state);

        switch (status) {
            case CURRENT:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByBookerIdAndCurrentTime(booker.getId(), LocalDateTime.now()));
            case PAST:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByBookerAndEndBefore(booker, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByBookerAndStartAfter(booker, LocalDateTime.now()));
            case WAITING:
            case REJECTED:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByBookerAndStatus(booker, status));
            default:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByBooker(booker));
        }
    }

    @Override
    @Transactional
    public List<BookingDtoResponse> getOwnerBookings(int userId, String state) {
        User owner = userRepository.getExistingUser(userId);
        BookingStatus status = BookingStatus.fromString(state);

        switch (status) {
            case CURRENT:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByOwnerIdAndCurrentTime(owner.getId(), LocalDateTime.now()));
            case PAST:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByItemOwnerAndEndBefore(owner, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByItemOwnerAndStartAfter(owner, LocalDateTime.now()));
            case WAITING:
            case REJECTED:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByItemOwnerAndStatus(owner, status));
            default:
                return BookingMapper.bookingDtoResponseListSorted(bookingRepository.findByItemOwner(owner));
        }
    }

    private void requestValidation(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            log.warn("Предмет с ID: {} - {} недоступен для бронирования", booking.getItem().getId(), booking.getItem());
            throw new NotAvailableException("Предмет недоступен для бронирования");
        }
        if (booking.getBooker().equals(booking.getItem().getOwner())) {
            log.warn("Владелец вещи не может оставить бронь на свою же вещь");
            throw new BookingNotFoundException("Владелец вещи не может оставить бронь на свою же вещь");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            log.warn("Бронирование {} не может начинаться позже даты окончания", booking);
            throw new DateException("Дата окончания бронирования не может быть раньше даты начала");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            log.warn("Бронирование {} не может начинаться и заканчиваться в одно и то же время", booking);
            throw new DateException("Дата начала и дата окончания бронирования совпадают");
        }
    }
}
