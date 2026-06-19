package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private ItemRequestDto createRequestDto(Long id) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(id);
        dto.setDescription("Need a drill");
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

    @Test
    void createItemRequest_shouldReturn200() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");

        when(itemRequestClient.create(any(Long.class), any()))
                .thenReturn(ResponseEntity.ok(createRequestDto(1L)));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequests_shouldReturn200() throws Exception {
        when(itemRequestClient.getAll(any(Long.class)))
                .thenReturn(ResponseEntity.ok(List.of(createRequestDto(1L))));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemRequestsByUser_shouldReturn200() throws Exception {
        when(itemRequestClient.getAllByUserId(any(Long.class)))
                .thenReturn(ResponseEntity.ok(List.of(createRequestDto(1L))));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemRequest_shouldReturn200() throws Exception {
        when(itemRequestClient.getItemRequest(any(Long.class), any(Long.class)))
                .thenReturn(ResponseEntity.ok(createRequestDto(1L)));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}