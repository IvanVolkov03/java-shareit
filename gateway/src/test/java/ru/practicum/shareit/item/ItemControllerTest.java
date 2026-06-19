package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void createItem_shouldReturn200() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Drill", "Power drill", true, null, null, null, null);
        when(itemClient.create(any(Long.class), any()))
                .thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_shouldReturn200() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Updated", "Updated desc", true, null, null, null, null);
        when(itemClient.update(any(Long.class), any(Long.class), any()))
                .thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItem_shouldReturn200() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Drill", "Power drill", true, null, null, null, null);
        when(itemClient.getItem(1L, 1L))
                .thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItems_shouldReturn200() throws Exception {
        List<ItemDto> items = List.of(new ItemDto(1L, "Drill", "Power drill", true, null, null, null, null));
        when(itemClient.getItems(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(ResponseEntity.ok(items));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_shouldReturn200() throws Exception {
        List<ItemDto> items = List.of(new ItemDto(1L, "Drill", "Power drill", true, null, null, null, null));
        when(itemClient.search("drill"))
                .thenReturn(ResponseEntity.ok(items));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturn200() throws Exception {
        when(itemClient.addComment(any(Long.class), any(Long.class), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Great item!\"}"))
                .andExpect(status().isOk());
    }
}