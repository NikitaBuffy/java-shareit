package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Booking> findByBookerAndStatus(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findByBooker(User booker, Pageable pageable);

    Page<Booking> findByBookerAndEndBefore(User booker, LocalDateTime currentTime, Pageable pageable);

    List<Booking> findByBookerAndItemIdAndEndBeforeAndStatus(User booker, Long itemId,
                                                             LocalDateTime currentTime, BookingStatus status);

    Page<Booking> findByBookerAndStartAfter(User booker, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND :currentTime BETWEEN b.start AND b.end")
    Page<Booking> findByBookerIdAndCurrentTime(Long bookerId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findByItemOwnerAndStatus(User owner, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemOwner(User owner, Pageable pageable);

    Page<Booking> findByItemOwnerAndEndBefore(User owner, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findByItemOwnerAndStartAfter(User owner, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN b.item i " +
            "WHERE i.owner.id = :ownerId " +
            "AND :currentTime BETWEEN b.start AND b.end")
    Page<Booking> findByOwnerIdAndCurrentTime(Long ownerId, LocalDateTime currentTime, Pageable pageable);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime currentTime, BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStart(Long itemId, LocalDateTime currentTime, BookingStatus status);

    default Booking getExistingBooking(Long bookingId) {
        return findById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException("Бронирование с ID: " + bookingId + " не существует");
        });
    }
}
