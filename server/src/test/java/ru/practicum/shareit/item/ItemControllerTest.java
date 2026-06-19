package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private ItemService itemService;

    @Test
    void create_shouldReturnCreatedItem() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Drill", "Power drill", true, null, null, null, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Power drill", true, null, null, null, null);

        when(itemService.create(any(ItemDto.class), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Drill", "Power drill", true, null, null, null, null);
        when(itemService.getById(1L, 1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getByOwner_shouldReturnItemsList() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Item 1", "Desc", true, null, null, null, null),
                new ItemDto(2L, "Item 2", "Desc", true, null, null, null, null)
        );
        when(itemService.getByOwnerId(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void search_shouldReturnItems() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Drill", "Desc", true, null, null, null, null)
        );
        when(itemService.search("drill")).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Updated", "Updated desc", false, null, null, null, null);
        ItemDto responseDto = new ItemDto(1L, "Updated", "Updated desc", false, null, null, null, null);

        when(itemService.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        CommentDto requestDto = new CommentDto(null, "Great item!", null, null);
        CommentDto responseDto = new CommentDto(1L, "Great item!", "John", null);

        when(itemService.addComment(eq(1L), eq(1L), eq("Great item!"))).thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("John"));
    }
}