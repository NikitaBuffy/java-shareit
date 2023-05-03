package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerAndStatus(User booker, BookingStatus status);

    List<Booking> findByBooker(User booker);

    List<Booking> findByBookerAndEndBefore(User booker, LocalDateTime currentTime);

    List<Booking> findByBookerAndItemIdAndEndBeforeAndStatus(User booker, int itemId,
                                                             LocalDateTime currentTime, BookingStatus status);

    List<Booking> findByBookerAndStartAfter(User booker, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE booker_id = :bookerId " +
            "AND :currentTime BETWEEN start_date AND end_date",
            nativeQuery = true)
    List<Booking> findByBookerIdAndCurrentTime(int bookerId, LocalDateTime currentTime);

    List<Booking> findByItemOwnerAndStatus(User owner, BookingStatus status);

    List<Booking> findByItemOwner(User owner);

    List<Booking> findByItemOwnerAndEndBefore(User owner, LocalDateTime currentTime);

    List<Booking> findByItemOwnerAndStartAfter(User owner, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings b " +
            "LEFT JOIN items i on b.item_id = i.item_id " +
            "WHERE i.owner_id = :ownerId " +
            "AND :currentTime BETWEEN start_date AND end_date",
            nativeQuery = true)
    List<Booking> findByOwnerIdAndCurrentTime(int ownerId, LocalDateTime currentTime);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(int itemId, LocalDateTime currentTime, BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStart(int itemId, LocalDateTime currentTime, BookingStatus status);

    default Booking getExistingBooking(int bookingId) {
        return findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException("Бронирование с ID: " + bookingId + " не существует");
        });
    }
}
