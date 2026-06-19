package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;

class ItemDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Power drill for home use");
        itemDto.setAvailable(true);
        itemDto.setRequestId(10L);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setComments(Collections.emptyList());

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Drill\"");
        assertThat(json).contains("\"description\":\"Power drill for home use\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":10");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{" +
                "\"id\":1," +
                "\"name\":\"Drill\"," +
                "\"description\":\"Power drill\"," +
                "\"available\":true," +
                "\"requestId\":null," +
                "\"lastBooking\":null," +
                "\"nextBooking\":null," +
                "\"comments\":[]" +
                "}";

        ItemDto itemDto = objectMapper.readValue(jsonContent, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Drill");
        assertThat(itemDto.getDescription()).isEqualTo("Power drill");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isNull();
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isEmpty();
    }

    @Test
    void testSerializeWithBookings() throws Exception {
        LocalDateTime lastStart = LocalDateTime.of(2024, 5, 1, 10, 0);
        LocalDateTime lastEnd = LocalDateTime.of(2024, 5, 3, 18, 0);
        LocalDateTime nextStart = LocalDateTime.of(2024, 6, 10, 9, 0);
        LocalDateTime nextEnd = LocalDateTime.of(2024, 6, 12, 17, 0);
        BookingShortDto lastBooking = new BookingShortDto(1L, 5L, lastStart, lastEnd);
        BookingShortDto nextBooking = new BookingShortDto(2L, 10L, nextStart, nextEnd);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Hammer");
        itemDto.setDescription("Steel hammer");
        itemDto.setAvailable(true);
        itemDto.setRequestId(null);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(Collections.emptyList());

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"lastBooking\"");
        assertThat(json).contains("\"nextBooking\"");
        assertThat(json).contains("2024-05-01T10:00:00");
        assertThat(json).contains("2024-06-10T09:00:00");
    }

    @Test
    void testDeserializeWithComments() throws Exception {
        String jsonContent = "{" +
                "\"id\":1," +
                "\"name\":\"Drill\"," +
                "\"description\":\"Power drill\"," +
                "\"available\":true," +
                "\"requestId\":null," +
                "\"lastBooking\":null," +
                "\"nextBooking\":null," +
                "\"comments\":[" +
                "{\"id\":1,\"text\":\"Great item!\",\"authorName\":\"John\",\"created\":\"2024-05-15T14:30:00\"}," +
                "{\"id\":2,\"text\":\"Very useful\",\"authorName\":\"Jane\",\"created\":\"2024-05-16T10:00:00\"}" +
                "]" +
                "}";

        ItemDto itemDto = objectMapper.readValue(jsonContent, ItemDto.class);

        assertThat(itemDto.getComments()).hasSize(2);
        assertThat(itemDto.getComments().get(0).getText()).isEqualTo("Great item!");
        assertThat(itemDto.getComments().get(0).getAuthorName()).isEqualTo("John");
        assertThat(itemDto.getComments().get(1).getText()).isEqualTo("Very useful");
        assertThat(itemDto.getComments().get(1).getAuthorName()).isEqualTo("Jane");
    }

    @Test
    void testSerializeWithRequestId() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(5L);
        itemDto.setName("Saw");
        itemDto.setDescription("Wood saw");
        itemDto.setAvailable(false);
        itemDto.setRequestId(100L);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        itemDto.setComments(Collections.emptyList());

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"requestId\":100");
        assertThat(json).contains("\"available\":false");
    }

    @Test
    void testConstructorWithFiveParameters() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Drill", "Power drill", true, 10L);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Drill");
        assertThat(itemDto.getDescription()).isEqualTo("Power drill");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(10L);
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isNull();
    }
}