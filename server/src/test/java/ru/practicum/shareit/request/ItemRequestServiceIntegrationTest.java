package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(new User(null, "User", "user@example.com"));
    }

    @Test
    void create_shouldSaveRequestToDatabase() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");

        ItemRequestDto result = itemRequestService.create(user.getId(), requestDto);

        assertNotNull(result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertNotNull(result.getCreated());
    }

    @Test
    void getAllByUserId_shouldReturnUserRequests() {
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Need a drill");
        itemRequestService.create(user.getId(), requestDto1);

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Need a hammer");
        itemRequestService.create(user.getId(), requestDto2);
        List<ItemRequestDto> result = itemRequestService.getAllByUserId(user.getId());

        assertEquals(2, result.size());
    }
}