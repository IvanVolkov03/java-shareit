package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto createBookingDto(Long id, BookingStatus status) {
        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setItemId(1L);
        dto.setBookerId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(status);
        return dto;
    }

    @Test
    void create_shouldReturnCreatedBooking() throws Exception {
        BookingDto requestDto = new BookingDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto responseDto = createBookingDto(1L, BookingStatus.WAITING);

        when(bookingService.create(any(BookingDto.class), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void updateStatus_shouldReturnApprovedBooking() throws Exception {
        BookingDto responseDto = createBookingDto(1L, BookingStatus.APPROVED);

        when(bookingService.updateStatus(eq(1L), eq(1L), eq(true))).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        BookingDto bookingDto = createBookingDto(1L, BookingStatus.WAITING);

        when(bookingService.getById(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getByBooker_shouldReturnListOfBookings() throws Exception {
        List<BookingDto> bookings = List.of(createBookingDto(1L, BookingStatus.WAITING));

        when(bookingService.getByBooker(eq(1L), eq("ALL"))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getByOwner_shouldReturnListOfBookings() throws Exception {
        List<BookingDto> bookings = List.of(createBookingDto(1L, BookingStatus.WAITING));

        when(bookingService.getByOwner(eq(1L), eq("ALL"))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}