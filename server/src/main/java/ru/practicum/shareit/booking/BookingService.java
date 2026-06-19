package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, Long bookerId);

    BookingDto updateStatus(Long bookingId, Long userId, boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getByBooker(Long bookerId, String state);

    List<BookingDto> getByOwner(Long ownerId, String state);
}