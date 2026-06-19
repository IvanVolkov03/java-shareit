package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class CommentDtoTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
        CommentDto commentDto = new CommentDto(1L, "Great item, works perfectly!", "John Doe", created);

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Great item, works perfectly!\"");
        assertThat(json).contains("\"authorName\":\"John Doe\"");
        assertThat(json).contains("2024-06-15T14:30:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{" +
                "\"id\":1," +
                "\"text\":\"Great item!\"," +
                "\"authorName\":\"John Doe\"," +
                "\"created\":\"2024-06-15T14:30:00\"" +
                "}";

        CommentDto commentDto = objectMapper.readValue(jsonContent, CommentDto.class);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("Great item!");
        assertThat(commentDto.getAuthorName()).isEqualTo("John Doe");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 6, 15, 14, 30, 0));
    }

    @Test
    void testSerializeWithSpecialCharacters() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 7, 20, 10, 15, 0);
        CommentDto commentDto = new CommentDto(
                2L,
                "Excellent! Works great with \"special\" characters & symbols",
                "Jane O'Brien",
                created
        );

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains("\"id\":2");
        assertThat(json).contains("special");
        assertThat(json).contains("Jane O'Brien");
    }

    @Test
    void testDeserializeWithLongText() throws Exception {
        String longText = "This is a very long comment that describes the item in great detail. " +
                "It covers multiple aspects of the product including quality, usability, and value for money. " +
                "The item exceeded my expectations and I would definitely recommend it to others.";

        String jsonContent = "{" +
                "\"id\":3," +
                "\"text\":\"" + longText + "\"," +
                "\"authorName\":\"Bob Johnson\"," +
                "\"created\":\"2024-08-10T16:45:00\"" +
                "}";

        CommentDto commentDto = objectMapper.readValue(jsonContent, CommentDto.class);

        assertThat(commentDto.getText()).isEqualTo(longText);
        assertThat(commentDto.getText().length()).isGreaterThan(200);
    }

    @Test
    void testSerializeWithNullFields() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Test comment", null, null);

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Test comment\"");
        assertThat(json).contains("\"authorName\":null");
        assertThat(json).contains("\"created\":null");
    }

    @Test
    void testDeserializeWithDifferentDateTimeFormat() throws Exception {
        String jsonContent = "{" +
                "\"id\":4," +
                "\"text\":\"Good product\"," +
                "\"authorName\":\"Alice\"," +
                "\"created\":\"2024-09-05T08:00:00\"" +
                "}";

        CommentDto commentDto = objectMapper.readValue(jsonContent, CommentDto.class);

        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 9, 5, 8, 0, 0));
    }
}