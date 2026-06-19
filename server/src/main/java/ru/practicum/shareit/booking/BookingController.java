package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(
            @RequestHeader("X-Sharer-User-Id") @Positive Long bookerId,
            @RequestBody BookingDto bookingDto
    ) {
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestParam Boolean approved
    ) {
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId
    ) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getByBooker(
            @RequestHeader("X-Sharer-User-Id") @Positive Long bookerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(
            @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getByOwner(ownerId, state);
    }
}