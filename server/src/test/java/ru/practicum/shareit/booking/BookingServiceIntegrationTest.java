package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        booker = userRepository.save(new User(null, "Booker", "booker@example.com"));
        item = itemRepository.save(new Item(null, "Drill", "Power drill", true, owner, null));
    }

    @Test
    void create_shouldSaveBookingToDatabase() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.create(bookingDto, booker.getId());

        assertNotNull(result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());

        Booking fromDb = bookingRepository.findById(result.getId()).orElseThrow();
        assertEquals(booker.getId(), fromDb.getBooker().getId());
        assertEquals(item.getId(), fromDb.getItem().getId());
    }

    @Test
    void updateStatus_shouldApproveBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto created = bookingService.create(bookingDto, booker.getId());

        BookingDto result = bookingService.updateStatus(created.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void getByBooker_shouldReturnUserBookings() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(bookingDto, booker.getId());

        List<BookingDto> result = bookingService.getByBooker(booker.getId(), "ALL");

        assertEquals(1, result.size());
    }
}