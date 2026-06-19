package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class BookingDtoTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testSerialize() throws Exception {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 5, 18, 0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(10L);
        bookingDto.setBookerId(5L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setItem(new ItemShortDto(10L, "Drill"));
        bookingDto.setBooker(new BookerDto(5L, "John Doe"));

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"itemId\":10");
        assertThat(json).contains("\"bookerId\":5");
        assertThat(json).contains("2024-06-01T10:00:00");
        assertThat(json).contains("2024-06-05T18:00:00");
        assertThat(json).contains("WAITING");
        assertThat(json).contains("Drill");
        assertThat(json).contains("John Doe");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{" +
                "\"id\":1," +
                "\"itemId\":10," +
                "\"bookerId\":5," +
                "\"start\":\"2024-06-01T10:00:00\"," +
                "\"end\":\"2024-06-05T18:00:00\"," +
                "\"status\":\"WAITING\"," +
                "\"item\":{\"id\":10,\"name\":\"Drill\"}," +
                "\"booker\":{\"id\":5,\"name\":\"John Doe\"}" +
                "}";

        BookingDto bookingDto = objectMapper.readValue(jsonContent, BookingDto.class);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getItemId()).isEqualTo(10L);
        assertThat(bookingDto.getBookerId()).isEqualTo(5L);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 6, 5, 18, 0));
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingDto.getItem()).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(10L);
        assertThat(bookingDto.getItem().getName()).isEqualTo("Drill");
        assertThat(bookingDto.getBooker()).isNotNull();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(5L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("John Doe");
    }

    @Test
    void testSerializeWithNullFields() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(10L);
        bookingDto.setBookerId(5L);
        bookingDto.setStart(LocalDateTime.of(2024, 6, 1, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2024, 6, 5, 18, 0));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setItem(null);
        bookingDto.setBooker(null);

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"item\":null");
        assertThat(json).contains("\"booker\":null");
    }

    @Test
    void testDeserializeWithApprovedStatus() throws Exception {
        String jsonContent = "{" +
                "\"id\":2," +
                "\"itemId\":20," +
                "\"bookerId\":10," +
                "\"start\":\"2024-07-01T09:00:00\"," +
                "\"end\":\"2024-07-10T17:00:00\"," +
                "\"status\":\"APPROVED\"," +
                "\"item\":{\"id\":20,\"name\":\"Hammer\"}," +
                "\"booker\":{\"id\":10,\"name\":\"Jane Smith\"}" +
                "}";

        BookingDto bookingDto = objectMapper.readValue(jsonContent, BookingDto.class);

        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testDeserializeWithRejectedStatus() throws Exception {
        String jsonContent = "{" +
                "\"id\":3," +
                "\"itemId\":30," +
                "\"bookerId\":15," +
                "\"start\":\"2024-08-01T12:00:00\"," +
                "\"end\":\"2024-08-05T15:00:00\"," +
                "\"status\":\"REJECTED\"," +
                "\"item\":{\"id\":30,\"name\":\"Saw\"}," +
                "\"booker\":{\"id\":15,\"name\":\"Bob Johnson\"}" +
                "}";

        BookingDto bookingDto = objectMapper.readValue(jsonContent, BookingDto.class);

        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }
}