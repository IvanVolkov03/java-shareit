package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating item request: {}", itemRequestDto);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting all item requests for user: {}", userId);
        return itemRequestClient.getAll(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting all item requests by user: {}", userId);
        return itemRequestClient.getAllByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Getting item request: {}", requestId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}