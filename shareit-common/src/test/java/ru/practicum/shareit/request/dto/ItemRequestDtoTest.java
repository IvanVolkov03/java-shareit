package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemShortDto;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestDtoTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 6, 1, 10, 0);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill for weekend project");
        requestDto.setCreated(created);
        requestDto.setItems(Collections.emptyList());
        requestDto.setRequestor(new RequestorDto(5L, "John Doe"));

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill for weekend project\"");
        assertThat(json).contains("2024-06-01T10:00:00");
        assertThat(json).contains("\"items\":[]");
        assertThat(json).contains("\"requestor\"");
        assertThat(json).contains("John Doe");
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{" +
                "\"id\":1," +
                "\"description\":\"Need a drill\"," +
                "\"created\":\"2024-06-01T10:00:00\"," +
                "\"items\":[]," +
                "\"requestor\":{\"id\":5,\"name\":\"John Doe\"}" +
                "}";

        ItemRequestDto requestDto = objectMapper.readValue(jsonContent, ItemRequestDto.class);

        assertThat(requestDto.getId()).isEqualTo(1L);
        assertThat(requestDto.getDescription()).isEqualTo("Need a drill");
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 6, 1, 10, 0));
        assertThat(requestDto.getItems()).isEmpty();
        assertThat(requestDto.getRequestor()).isNotNull();
        assertThat(requestDto.getRequestor().getId()).isEqualTo(5L);
        assertThat(requestDto.getRequestor().getName()).isEqualTo("John Doe");
    }

    @Test
    void testSerializeWithItems() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 6, 1, 10, 0);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need tools");
        requestDto.setCreated(created);
        requestDto.setItems(Arrays.asList(
                new ItemShortDto(10L, "Drill"),
                new ItemShortDto(20L, "Hammer")
        ));
        requestDto.setRequestor(new RequestorDto(5L, "John Doe"));

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"items\"");
        assertThat(json).contains("\"Drill\"");
        assertThat(json).contains("\"Hammer\"");
        assertThat(json).contains("\"id\":10");
        assertThat(json).contains("\"id\":20");
    }

    @Test
    void testDeserializeWithMultipleItems() throws Exception {
        String jsonContent = "{" +
                "\"id\":2," +
                "\"description\":\"Need construction tools\"," +
                "\"created\":\"2024-07-15T14:30:00\"," +
                "\"items\":[" +
                "{\"id\":10,\"name\":\"Drill\"}," +
                "{\"id\":20,\"name\":\"Hammer\"}," +
                "{\"id\":30,\"name\":\"Saw\"}" +
                "]," +
                "\"requestor\":{\"id\":10,\"name\":\"Jane Smith\"}" +
                "}";

        ItemRequestDto requestDto = objectMapper.readValue(jsonContent, ItemRequestDto.class);

        assertThat(requestDto.getId()).isEqualTo(2L);
        assertThat(requestDto.getDescription()).isEqualTo("Need construction tools");
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 7, 15, 14, 30));
        assertThat(requestDto.getItems()).hasSize(3);
        assertThat(requestDto.getItems().get(0).getName()).isEqualTo("Drill");
        assertThat(requestDto.getItems().get(1).getName()).isEqualTo("Hammer");
        assertThat(requestDto.getItems().get(2).getName()).isEqualTo("Saw");
        assertThat(requestDto.getRequestor().getName()).isEqualTo("Jane Smith");
    }

    @Test
    void testSerializeWithNullRequestor() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need something");
        requestDto.setCreated(LocalDateTime.of(2024, 6, 1, 10, 0));
        requestDto.setItems(Collections.emptyList());
        requestDto.setRequestor(null);

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"requestor\":null");
    }

    @Test
    void testDeserializeWithNullItems() throws Exception {
        String jsonContent = "{" +
                "\"id\":3," +
                "\"description\":\"Need help\"," +
                "\"created\":\"2024-08-01T09:00:00\"," +
                "\"items\":null," +
                "\"requestor\":{\"id\":15,\"name\":\"Bob Johnson\"}" +
                "}";

        ItemRequestDto requestDto = objectMapper.readValue(jsonContent, ItemRequestDto.class);

        assertThat(requestDto.getItems()).isNull();
    }
}