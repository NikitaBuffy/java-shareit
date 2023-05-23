package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED,
    ALL,
    CURRENT,
    PAST,
    FUTURE;

    public static BookingStatus fromString(String state) {
        if (state != null) {
            for (BookingStatus status : BookingStatus.values()) {
                if (status.name().equals(state)) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Unknown state: " + state);
    }
}
