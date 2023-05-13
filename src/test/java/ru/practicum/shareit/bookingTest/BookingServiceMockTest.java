package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingSort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceMockTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Item item;
    private User user;
    private Booking booking;
    private BookingDtoRequest bookingDtoRequest;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "email@mail.ru");
        item = new Item(1L, "name", "description", true, user, null);
        bookingDtoRequest = new BookingDtoRequest(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), 1L);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), item, user, BookingStatus.WAITING);
    }

    @Test
    void requestBooking_whenBookerIsOwner_thenBookingNotFoundExceptionThrown() {
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);

        assertThrows(BookingNotFoundException.class, () -> bookingService.requestBooking(user.getId(), bookingDtoRequest));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void requestBooking_whenItemNotAvailable_thenNotAvailableExceptionThrown() {
        item.setAvailable(false);
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);

        assertThrows(NotAvailableException.class, () -> bookingService.requestBooking(user.getId(), bookingDtoRequest));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void requestBooking_whenBookingStartIsAfterEnd_thenDateExceptionThrown() {
        bookingDtoRequest.setStart(LocalDateTime.now().plusMinutes(2));
        bookingDtoRequest.setEnd(LocalDateTime.now());
        when(userRepository.getExistingUser(anyLong())).thenReturn(new User());
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);

        assertThrows(DateException.class, () -> bookingService.requestBooking(2L, bookingDtoRequest));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void requestBooking_whenBookingStartEqualsEnd_thenDateExceptionThrown() {
        bookingDtoRequest.setEnd(bookingDtoRequest.getStart());
        when(userRepository.getExistingUser(anyLong())).thenReturn(new User());
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);

        assertThrows(DateException.class, () -> bookingService.requestBooking(2L, bookingDtoRequest));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void requestBooking_whenBookingValidated_thenReturnedBooking() {
        when(userRepository.getExistingUser(anyLong())).thenReturn(new User());
        when(itemRepository.getExistingItem(anyLong())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(BookingMapper.requestDtoToBooking(bookingDtoRequest));

        BookingDtoResponse booking = bookingService.requestBooking(2L, bookingDtoRequest);

        verify(bookingRepository, times(1)).save(any(Booking.class));
        assertNotNull(booking);
        assertEquals(1L, booking.getItem().getId());
    }

    @Test
    void confirmBooking_whenBookingNotFound_thenBookingNotFoundExceptionThrown() {
        when(bookingRepository.getExistingBooking(anyLong())).thenThrow(BookingNotFoundException.class);

        assertThrows(BookingNotFoundException.class, () -> bookingService.confirmBooking(1L, 1L, true));
    }

    @Test
    void confirmBooking_whenUserNotOwner_thenNotOwnerExceptionThrown() {
        item.setOwner(new User());
        when(bookingRepository.getExistingBooking(anyLong())).thenReturn(booking);

        assertThrows(NotOwnerException.class, () -> bookingService.confirmBooking(user.getId(), 1L, true));
    }

    @Test
    void confirmBooking_whenBookingAlreadyConfirmed_thenAlreadyApprovedExceptionThrown() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.getExistingBooking(anyLong())).thenReturn(booking);
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);

        assertThrows(AlreadyApprovedException.class, () -> bookingService.confirmBooking(user.getId(), 1L, true));
    }

    @Test
    void confirmBooking_whenBookingConfirmed_thenReturnedConfirmedBooking() {
        when(bookingRepository.getExistingBooking(anyLong())).thenReturn(booking);
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);

        BookingDtoResponse booking = bookingService.confirmBooking(user.getId(), 1L, true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void confirmBooking_whenBookingRejected_thenReturnedRejectedBooking() {
        when(bookingRepository.getExistingBooking(anyLong())).thenReturn(booking);
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);

        BookingDtoResponse booking = bookingService.confirmBooking(user.getId(), 1L, false);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void getBooking_whenUserNotAssociated_thenNotOwnerExceptionThrown() {
        booking.setBooker(new User());
        item.setOwner(new User());
        when(bookingRepository.getExistingBooking(anyLong())).thenReturn(booking);

        assertThrows(NotOwnerException.class, () -> bookingService.getBooking(user.getId(), 1L));
    }

    @Test
    void getUserBookings_whenWithState_thenReturnedBookingList() {
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(bookingRepository.findByBookerIdAndCurrentTime(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerAndEndBefore(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByBookerAndStartAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<BookingDtoResponse> currentBooking = bookingService.getUserBookings(1L,"CURRENT",
                0, 10, BookingSort.START_DESC);
        List<BookingDtoResponse> pastBooking = bookingService.getUserBookings(1L,"PAST",
                0, 10, BookingSort.START_DESC);
        List<BookingDtoResponse> futureBooking = bookingService.getUserBookings(1L,"FUTURE",
                0, 10, BookingSort.START_DESC);
        List<BookingDtoResponse> rejectedBooking = bookingService.getUserBookings(1L,"REJECTED",
                0, 10, BookingSort.START_DESC);

        assertEquals(1, currentBooking.size());
        assertEquals(1, pastBooking.size());
        assertEquals(1, rejectedBooking.size());
        assertEquals(0, futureBooking.size());
    }

    @Test
    void getOwnerBookings_whenWithState_thenReturnedBookingList() {
        when(userRepository.getExistingUser(anyLong())).thenReturn(user);
        when(bookingRepository.findByOwnerIdAndCurrentTime(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItemOwnerAndEndBefore(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItemOwnerAndStatus(any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        when(bookingRepository.findByItemOwnerAndStartAfter(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<BookingDtoResponse> currentBooking = bookingService.getOwnerBookings(1L,"CURRENT",
                0, 10, BookingSort.START_DESC);
        List<BookingDtoResponse> pastBooking = bookingService.getOwnerBookings(1L,"PAST",
                0, 10, BookingSort.START_DESC);
        List<BookingDtoResponse> futureBooking = bookingService.getOwnerBookings(1L,"FUTURE",
                0, 10, BookingSort.START_DESC);
        List<BookingDtoResponse> rejectedBooking = bookingService.getOwnerBookings(1L,"REJECTED",
                0, 10, BookingSort.START_DESC);

        assertEquals(1, currentBooking.size());
        assertEquals(1, pastBooking.size());
        assertEquals(1, rejectedBooking.size());
        assertEquals(0, futureBooking.size());
    }
}