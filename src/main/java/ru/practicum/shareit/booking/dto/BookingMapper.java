package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public static BookingDtoResponse bookingToDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingDtoResponse.Item(
                        booking.getItem().getId(),
                        booking.getItem().getName()
                ),
                new BookingDtoResponse.User(
                        booking.getBooker().getId(),
                        booking.getBooker().getName()
                ),
                booking.getStatus()
        );
    }

    public static Booking requestDtoToBooking(BookingDtoRequest bookingDtoRequest) {
        return new Booking(
                bookingDtoRequest.getId(),
                bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd(),
                null,
                null,
                BookingStatus.WAITING
        );
    }

    public static List<BookingDtoResponse> bookingDtoResponseList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::bookingToDtoResponse)
                .collect(Collectors.toList());
    }
}
