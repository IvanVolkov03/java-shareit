package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestorDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequestDto.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(request);

        return itemRequestRepository.findById(saved.getId())
                .map(this::toItemRequestDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load saved request"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAll() {
        return itemRequestRepository.findAllByOrderByCreatedDesc().stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getById(Long userId, Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
        return toItemRequestDto(request);
    }

    private ItemRequestDto toItemRequestDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(itemRepository.findByRequestId(request.getId()).stream()
                .map(this::toItemShortDto)
                .collect(Collectors.toList()));

        if (request.getRequestor() != null) {
            dto.setRequestor(new RequestorDto(
                    request.getRequestor().getId(),
                    request.getRequestor().getName()
            ));
        }

        return dto;
    }

    private ItemShortDto toItemShortDto(Item item) {
        return new ItemShortDto(item.getId(), item.getName());
    }
}