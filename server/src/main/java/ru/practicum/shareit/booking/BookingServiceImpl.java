package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(BookingDto bookingDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (!item.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item is not available");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end dates are required");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End date must be after start date");
        }

        if (bookingDto.getEnd().isBefore(LocalDateTime.now()) ||
                bookingDto.getEnd().isEqual(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End date cannot be in the past");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long bookingId, Long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can approve/reject booking");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only change WAITING bookings");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long bookerId, String state) {
        if (!userRepository.existsById(bookerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
        return filterBookings(bookings, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwner(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        return filterBookings(bookings, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> filterBookings(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();
        String stateUpper = state != null ? state.toUpperCase() : "ALL";

        return switch (stateUpper) {
            case "ALL" -> bookings;
            case "CURRENT" -> bookings.stream()
                    .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                    .collect(Collectors.toList());
            case "PAST" -> bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now))
                    .collect(Collectors.toList());
            case "FUTURE" -> bookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .collect(Collectors.toList());
            case "WAITING" -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.WAITING)
                    .collect(Collectors.toList());
            case "REJECTED" -> bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                    .collect(Collectors.toList());
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown state: " + state);
        };
    }
}