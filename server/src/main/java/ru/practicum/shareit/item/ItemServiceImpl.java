package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Available field is required");
        }

        Item item;
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ItemRequest not found"));
            item = ItemMapper.toItem(itemDto, request);
        } else {
            item = ItemMapper.toItem(itemDto);
        }

        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (existing.getOwner() == null || !existing.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can update item");
        }
        if (itemDto.getName() != null) existing.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existing.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existing.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(existing));
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        return enrichItemDto(ItemMapper.toItemDto(item), userId, item);
    }

    @Override
    public List<ItemDto> getByOwnerId(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            return List.of();
        }
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastBookingsMap = bookingRepository
                .findLastApprovedBookingsByItemIds(itemIds, now).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (existing, replacement) -> existing
                ));

        Map<Long, Booking> nextBookingsMap = bookingRepository
                .findNextApprovedBookingsByItemIds(itemIds, now).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (existing, replacement) -> existing
                ));

        Map<Long, List<Comment>> commentsMap = commentRepository
                .findByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);

                    Booking lastBooking = lastBookingsMap.get(item.getId());
                    BookingShortDto last = lastBooking != null ?
                            new BookingShortDto(lastBooking.getId(),
                                    lastBooking.getBooker().getId(),
                                    lastBooking.getStart(),
                                    lastBooking.getEnd()) : null;

                    Booking nextBooking = nextBookingsMap.get(item.getId());
                    BookingShortDto next = nextBooking != null ?
                            new BookingShortDto(nextBooking.getId(),
                                    nextBooking.getBooker().getId(),
                                    nextBooking.getStart(),
                                    nextBooking.getEnd()) : null;

                    List<CommentDto> comments = commentsMap.getOrDefault(item.getId(), List.of()).stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());

                    dto.setLastBooking(last);
                    dto.setNextBooking(next);
                    dto.setComments(comments);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, String text) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        LocalDateTime now = LocalDateTime.now();

        boolean hasApprovedBooking = bookingRepository.existsApprovedPastBooking(itemId, userId, now);

        if (!hasApprovedBooking) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot comment on item without approved past booking");
        }
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemDto enrichItemDto(ItemDto dto, Long currentUserId, Item item) {
        LocalDateTime now = LocalDateTime.now();

        BookingShortDto last = null;
        BookingShortDto next = null;

        if (currentUserId != null && item.getOwner() != null &&
                item.getOwner().getId().equals(currentUserId)) {

            Optional<Booking> lastBooking = bookingRepository.findLastApprovedBookingByItemId(dto.getId(), now);
            Optional<Booking> nextBooking = bookingRepository.findNextApprovedBookingByItemId(dto.getId(), now);

            last = lastBooking.map(b ->
                    new BookingShortDto(b.getId(), b.getBooker().getId(), b.getStart(), b.getEnd())).orElse(null);
            next = nextBooking.map(b ->
                    new BookingShortDto(b.getId(), b.getBooker().getId(), b.getStart(), b.getEnd())).orElse(null);
        }

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(dto.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        dto.setLastBooking(last);
        dto.setNextBooking(next);
        dto.setComments(comments);
        return dto;
    }
}