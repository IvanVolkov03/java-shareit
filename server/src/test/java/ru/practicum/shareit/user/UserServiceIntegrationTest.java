package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void create_shouldSaveUserToDatabase() {
        UserDto userDto = new UserDto(null, "John Doe", "john@example.com");

        UserDto result = userService.create(userDto);

        assertNotNull(result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());

        User fromDb = userRepository.findById(result.getId()).orElseThrow();
        assertEquals("John Doe", fromDb.getName());
    }

    @Test
    void update_shouldModifyExistingUser() {
        UserDto userDto = new UserDto(null, "John Doe", "john@example.com");
        UserDto created = userService.create(userDto);
        UserDto updateDto = new UserDto(null, "Jane Doe", null);
        UserDto result = userService.update(created.getId(), updateDto);

        assertEquals("Jane Doe", result.getName());
        assertEquals("john@example.com", result.getEmail()); // email не изменился
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        userService.create(new UserDto(null, "User 1", "user1@example.com"));
        userService.create(new UserDto(null, "User 2", "user2@example.com"));

        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
    }
}