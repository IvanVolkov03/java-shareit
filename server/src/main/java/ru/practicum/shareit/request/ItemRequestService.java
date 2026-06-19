package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByUserId(Long userId);

    List<ItemRequestDto> getAll();

    ItemRequestDto getById(Long userId, Long requestId);
}