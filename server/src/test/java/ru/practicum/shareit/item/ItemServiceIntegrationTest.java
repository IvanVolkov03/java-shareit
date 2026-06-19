package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
    }

    @Test
    void create_shouldSaveItemToDatabase() {
        ItemDto itemDto = new ItemDto(null, "Drill", "Power drill", true, null, null, null, null);

        ItemDto result = itemService.create(itemDto, owner.getId());

        assertNotNull(result.getId());
        assertEquals("Drill", result.getName());
        assertEquals("Power drill", result.getDescription());
        assertTrue(result.getAvailable());

        Item fromDb = itemRepository.findById(result.getId()).orElseThrow();
        assertEquals("Drill", fromDb.getName());
        assertEquals(owner.getId(), fromDb.getOwner().getId());
    }

    @Test
    void getByOwnerId_shouldReturnUserItems() {
        itemService.create(new ItemDto(null, "Item 1", "Desc 1", true, null, null, null, null), owner.getId());
        itemService.create(new ItemDto(null, "Item 2", "Desc 2", true, null, null, null, null), owner.getId());

        List<ItemDto> result = itemService.getByOwnerId(owner.getId());

        assertEquals(2, result.size());
    }

    @Test
    void search_shouldFindItemsByName() {
        itemService.create(new ItemDto(null, "Drill", "Power tool", true, null, null, null, null), owner.getId());
        itemService.create(new ItemDto(null, "Hammer", "Tool", true, null, null, null, null), owner.getId());

        List<ItemDto> result = itemService.search("drill");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }
}