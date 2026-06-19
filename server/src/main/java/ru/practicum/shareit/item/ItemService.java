package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    ItemDto getById(Long itemId, Long userId);

    List<ItemDto> getByOwnerId(Long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, Long userId, String text);
}