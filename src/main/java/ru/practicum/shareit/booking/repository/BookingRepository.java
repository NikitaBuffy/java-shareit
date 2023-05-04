package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerAndStatus(User booker, BookingStatus status);

    List<Booking> findByBooker(User booker);

    List<Booking> findByBookerAndEndBefore(User booker, LocalDateTime currentTime);

    List<Booking> findByBookerAndItemIdAndEndBeforeAndStatus(User booker, Long itemId,
                                                             LocalDateTime currentTime, BookingStatus status);

    List<Booking> findByBookerAndStartAfter(User booker, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings " +
            "WHERE booker_id = :bookerId " +
            "AND :currentTime BETWEEN start_date AND end_date",
            nativeQuery = true)
    List<Booking> findByBookerIdAndCurrentTime(Long bookerId, LocalDateTime currentTime);

    List<Booking> findByItemOwnerAndStatus(User owner, BookingStatus status);

    List<Booking> findByItemOwner(User owner);

    List<Booking> findByItemOwnerAndEndBefore(User owner, LocalDateTime currentTime);

    List<Booking> findByItemOwnerAndStartAfter(User owner, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings b " +
            "LEFT JOIN items i on b.item_id = i.item_id " +
            "WHERE i.owner_id = :ownerId " +
            "AND :currentTime BETWEEN start_date AND end_date",
            nativeQuery = true)
    List<Booking> findByOwnerIdAndCurrentTime(Long ownerId, LocalDateTime currentTime);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStart(Long itemId, LocalDateTime currentTime, BookingStatus status);

    default Booking getExistingBooking(Long bookingId) {
        return findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException("Бронирование с ID: " + bookingId + " не существует");
        });
    }
}
