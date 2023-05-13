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
    void findByBookerAndEndBefore_whenPastBookingExists_thenReturnedPastBooking() {
        booking.setStart(LocalDateTime.now().minusMinutes(20));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        List<Booking> pastBookings = bookingRepository.findByBookerAndEndBefore(booker, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, pastBookings.size());
    }

    @Test
    void findByBookerAndEndBefore_whenPastBookingNotExists_thenReturnedEmpty() {
        List<Booking> pastBookings = bookingRepository.findByBookerAndEndBefore(booker, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, pastBookings.size());
    }

    @Test
    void findByBookerAndStartAfter_whenFutureBookingExists_thenReturnedFutureBooking() {
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<Booking> futureBookings = bookingRepository.findByBookerAndStartAfter(booker, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, futureBookings.size());
    }

    @Test
    void findByBookerAndStartAfter_whenFutureBookingNotExists_thenReturnedEmptyList() {
        List<Booking> futureBookings = bookingRepository.findByBookerAndStartAfter(booker, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, futureBookings.size());
    }

    @Test
    void findByBookerIdAndCurrentTime_whenNotBooker_thenReturnedEmptyList() {
        booking.setBooker(owner);
        List<Booking> bookings = bookingRepository.findByBookerIdAndCurrentTime(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, bookings.size());
    }

    @Test
    void findByBookerIdAndCurrentTime_whenCurrentBookingExists_thenReturnedCurrentBooking() {
        List<Booking> currentBookings = bookingRepository.findByBookerIdAndCurrentTime(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, currentBookings.size());
    }

    @Test
    void findByBookerIdAndCurrentTime_whenCurrentBookingNotExists_thenReturnedEmptyList() {
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<Booking> currentBookings = bookingRepository.findByBookerIdAndCurrentTime(booker.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, currentBookings.size());
    }

    @Test
    void findByBookerAndStatus_whenStatusRejected_thenReturnedRejectedBooking() {
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> rejectedBookings = bookingRepository.findByBookerAndStatus(booker, BookingStatus.REJECTED,
                Pageable.unpaged()).getContent();

        assertEquals(1, rejectedBookings.size());
        assertEquals(BookingStatus.REJECTED, rejectedBookings.get(0).getStatus());
    }

    @Test
    void findByBookerAndStatus_whenStatusWaiting_thenReturnedWaitingBooking() {
        List<Booking> waitingBookings = bookingRepository.findByBookerAndStatus(booker, BookingStatus.WAITING,
                Pageable.unpaged()).getContent();

        assertEquals(1, waitingBookings.size());
        assertEquals(BookingStatus.WAITING, waitingBookings.get(0).getStatus());
    }

    @Test
    void findByBooker() {
        bookingRepository.save(new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), item, booker,
                BookingStatus.WAITING));
        List<Booking> allBookings = bookingRepository.findByBooker(booker, Pageable.unpaged()).getContent();

        assertEquals(2, allBookings.size());
    }

    //Owner
    @Test
    void findByOwnerIdAndCurrentTime_whenNotItemOwner_thenReturnedEmptyList() {
        item.setOwner(booker);
        List<Booking> currentBookings = bookingRepository.findByOwnerIdAndCurrentTime(owner.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, currentBookings.size());
    }

    @Test
    void findByOwnerIdAndCurrentTime_whenCurrentBookingExists_thenReturnedCurrentBooking() {
        List<Booking> currentBookings = bookingRepository.findByOwnerIdAndCurrentTime(owner.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, currentBookings.size());
    }

    @Test
    void findByOwnerIdAndCurrentTime_whenCurrentBookingNotExists_thenReturnedEmptyList() {
        booking.setStart(LocalDateTime.now().minusMinutes(20));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        List<Booking> currentBookings = bookingRepository.findByOwnerIdAndCurrentTime(owner.getId(), LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, currentBookings.size());
    }

    @Test
    void findByItemOwnerAndEndBefore_whenPastBookingExists_thenReturnedPastBooking() {
        booking.setStart(LocalDateTime.now().minusMinutes(20));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        List<Booking> pastBookings = bookingRepository.findByItemOwnerAndEndBefore(owner, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, pastBookings.size());
    }

    @Test
    void findByItemOwnerAndEndBefore_whenPastBookingNotExists_thenReturnedEmptyList() {
        List<Booking> pastBookings = bookingRepository.findByItemOwnerAndEndBefore(owner, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, pastBookings.size());
    }

    @Test
    void findByItemOwnerAndStartAfter_whenFutureBookingExists_thenReturnedFutureBooking() {
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        List<Booking> futureBookings = bookingRepository.findByItemOwnerAndStartAfter(owner, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(1, futureBookings.size());
    }

    @Test
    void findByItemOwnerAndStartAfter_whenFutureBookingNotExists_thenReturnedEmptyList() {
        List<Booking> futureBookings = bookingRepository.findByItemOwnerAndStartAfter(owner, LocalDateTime.now(),
                Pageable.unpaged()).getContent();

        assertEquals(0, futureBookings.size());
    }

    @Test
    void findByItemOwnerAndStatus_whenStatusWaiting_returnedWaitingBooking() {
        List<Booking> waitingBookings = bookingRepository.findByItemOwnerAndStatus(owner, BookingStatus.WAITING,
                Pageable.unpaged()).getContent();

        assertEquals(1, waitingBookings.size());
        assertEquals(BookingStatus.WAITING, waitingBookings.get(0).getStatus());
    }

    @Test
    void findByItemOwnerAndStatus_whenStatusRejected_returnedRejectedBooking() {
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> rejectedBookings = bookingRepository.findByItemOwnerAndStatus(owner, BookingStatus.REJECTED,
                Pageable.unpaged()).getContent();

        assertEquals(1, rejectedBookings.size());
        assertEquals(BookingStatus.REJECTED, rejectedBookings.get(0).getStatus());
    }

    @Test
    void findByItemOwner() {
        Item item2 = itemRepository.save(new Item(2L, "name2", "description2", true, owner, null));
        bookingRepository.save(new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), item2, booker,
                BookingStatus.WAITING));
        List<Booking> allBookings = bookingRepository.findByItemOwner(owner, Pageable.unpaged()).getContent();

        assertEquals(2, allBookings.size());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}