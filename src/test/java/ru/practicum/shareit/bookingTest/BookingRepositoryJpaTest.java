package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryJpaTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        owner = userRepository.save(new User(1L, "owner", "owner@email.ru"));
        booker = userRepository.save(new User(2L, "booker", "booker@mail.ru"));
        item = itemRepository.save(new Item(1L, "name", "description", true, owner, null));
        booking = bookingRepository.save(new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), item, booker,
                BookingStatus.WAITING));
    }

    //Booker
    @Test
    void shouldReturnPastBookingForBookerWhenPastBookingExists() {
        booking.setStart(LocalDateTime.now().minusMinutes(20));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        List<Booking> pastBookings = bookingRepository.findByBookerAndEndBefore(booker, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, pastBookings.size());
    }

    @Test
    void shouldReturnEmptyListForBookerWhenPastBookingNotExists() {
        List<Booking> pastBookings = bookingRepository.findByBookerAndEndBefore(booker, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, pastBookings.size());
    }

    @Test
    void shouldReturnFutureBookingForBookerWhenFutureBookingExists() {
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<Booking> futureBookings = bookingRepository.findByBookerAndStartAfter(booker, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, futureBookings.size());
    }

    @Test
    void shouldReturnEmptyListForBookerWhenFutureBookingNotExists() {
        List<Booking> futureBookings = bookingRepository.findByBookerAndStartAfter(booker, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, futureBookings.size());
    }

    @Test
    void shouldReturnEmptyListWhenNotBooker() {
        booking.setBooker(owner);
        List<Booking> bookings = bookingRepository.findByBookerIdAndCurrentTime(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, bookings.size());
    }

    @Test
    void shouldReturnCurrentBookingForBookerWhenCurrentBookingExists() {
        List<Booking> currentBookings = bookingRepository.findByBookerIdAndCurrentTime(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, currentBookings.size());
    }

    @Test
    void shouldReturnEmptyListForBookerWhenCurrentBookingNotExists() {
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<Booking> currentBookings = bookingRepository.findByBookerIdAndCurrentTime(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, currentBookings.size());
    }

    @Test
    void shouldReturnRejectedBookingForBookerWhenStatusRejected() {
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> rejectedBookings = bookingRepository.findByBookerAndStatus(booker, BookingStatus.REJECTED,
                Pageable.unpaged()).getContent();

        assertEquals(1, rejectedBookings.size());
        assertEquals(BookingStatus.REJECTED, rejectedBookings.get(0).getStatus());
    }

    @Test
    void shouldReturnWaitingBookingForBookerWhenStatusWaiting() {
        List<Booking> waitingBookings = bookingRepository.findByBookerAndStatus(booker, BookingStatus.WAITING,
                Pageable.unpaged()).getContent();

        assertEquals(1, waitingBookings.size());
        assertEquals(BookingStatus.WAITING, waitingBookings.get(0).getStatus());
    }

    @Test
    void shouldFindByBooker() {
        bookingRepository.save(new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), item, booker,
                BookingStatus.WAITING));
        List<Booking> allBookings = bookingRepository.findByBooker(booker, Pageable.unpaged()).getContent();

        assertEquals(2, allBookings.size());
    }

    //Owner
    @Test
    void shouldReturnEmptyListWhenNotItemOwner() {
        item.setOwner(booker);
        List<Booking> currentBookings = bookingRepository.findByOwnerIdAndCurrentTime(owner.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, currentBookings.size());
    }

    @Test
    void shouldReturnCurrentBookingForOwnerWhenCurrentBookingExists() {
        List<Booking> currentBookings = bookingRepository.findByOwnerIdAndCurrentTime(owner.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, currentBookings.size());
    }

    @Test
    void shouldReturnEmptyListForOwnerWhenCurrentBookingNotExists() {
        booking.setStart(LocalDateTime.now().minusMinutes(20));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        List<Booking> currentBookings = bookingRepository.findByOwnerIdAndCurrentTime(owner.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, currentBookings.size());
    }

    @Test
    void shouldReturnPastBookingForOwnerWhenPastBookingExists() {
        booking.setStart(LocalDateTime.now().minusMinutes(20));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        List<Booking> pastBookings = bookingRepository.findByItemOwnerAndEndBefore(owner, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, pastBookings.size());
    }

    @Test
    void shouldReturnEmptyListForOwnerWhenPastBookingNotExists() {
        List<Booking> pastBookings = bookingRepository.findByItemOwnerAndEndBefore(owner, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, pastBookings.size());
    }

    @Test
    void shouldReturnFutureBookingForOwnerWhenFutureBookingExists() {
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<Booking> futureBookings = bookingRepository.findByItemOwnerAndStartAfter(owner, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, futureBookings.size());
    }

    @Test
    void shouldReturnEmptyListForOwnerWhenFutureBookingNotExists() {
        List<Booking> futureBookings = bookingRepository.findByItemOwnerAndStartAfter(owner, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, futureBookings.size());
    }

    @Test
    void shouldReturnWaitingBookingForOwnerWhenStatusWaiting() {
        List<Booking> waitingBookings = bookingRepository.findByItemOwnerAndStatus(owner, BookingStatus.WAITING,
                Pageable.unpaged()).getContent();

        assertEquals(1, waitingBookings.size());
        assertEquals(BookingStatus.WAITING, waitingBookings.get(0).getStatus());
    }

    @Test
    void shouldReturnRejectedBookingForOwnerWhenStatusRejected() {
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> rejectedBookings = bookingRepository.findByItemOwnerAndStatus(owner, BookingStatus.REJECTED,
                Pageable.unpaged()).getContent();

        assertEquals(1, rejectedBookings.size());
        assertEquals(BookingStatus.REJECTED, rejectedBookings.get(0).getStatus());
    }

    @Test
    void shouldFindByItemOwner() {
        Item item2 = itemRepository.save(new Item(2L, "name2", "description2", true, owner, null));
        bookingRepository.save(new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), item2, booker,
                BookingStatus.WAITING));
        List<Booking> allBookings = bookingRepository.findByItemOwner(owner, Pageable.unpaged()).getContent();

        assertEquals(2, allBookings.size());
    }

    @Test
    void shouldReturnExistingBookingWhenBookingFound() {
        Booking bookingFound = bookingRepository.getExistingBooking(booking.getId());

        assertNotNull(bookingFound);
    }

    @Test
    void shouldThrowBookingNotFoundExceptionWhenBookingNotFound() {
        assertThrows(BookingNotFoundException.class, () -> bookingRepository.getExistingBooking(99L));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}