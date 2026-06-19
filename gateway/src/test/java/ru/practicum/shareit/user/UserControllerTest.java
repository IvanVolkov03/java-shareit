package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser_shouldReturn201() throws Exception {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto responseDto = new UserDto(1L, "John", "john@example.com");

        when(userClient.createUser(any()))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_shouldReturn400_whenEmailIsInvalid() throws Exception {
        UserDto userDto = new UserDto(null, "John", "invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_shouldReturn400_whenEmailIsEmpty() throws Exception {
        UserDto userDto = new UserDto(null, "John", "");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        UserDto userDto = new UserDto(null, "Jane", "jane@example.com");
        UserDto responseDto = new UserDto(1L, "Jane", "jane@example.com");

        when(userClient.updateUser(any(Long.class), any()))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getUser_shouldReturn200() throws Exception {
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        when(userClient.getUser(1L))
                .thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void getUsers_shouldReturn200() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "User 1", "user1@test.com"),
                new UserDto(2L, "User 2", "user2@test.com")
        );
        when(userClient.getUsers())
                .thenReturn(ResponseEntity.ok(users));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteUser_shouldReturn200() throws Exception {
        when(userClient.deleteUser(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}