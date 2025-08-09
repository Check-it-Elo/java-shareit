package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    //    @Override
//    public ItemDto create(Long ownerId, ItemDto dto) {
//        // Получаем DTO пользователя и конвертируем в модель
//        UserDto ownerDto = userService.getById(ownerId);
//        User owner = toUser(ownerDto);
//
//        ItemRequest req = (dto.getRequestId() == null) ? null :
//                new ItemRequest(dto.getRequestId(), null, null, null);
//
//        Item item = ItemMapper.toItem(dto, owner, req);
//        item.setId(seq.getAndIncrement());
//        storage.put(item.getId(), item);
//
//        return ItemMapper.toItemDto(item);
//    }
    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        // валидируем вход
        validateItemForCreate(dto);

        // проверяем владельца
        UserDto ownerDto = userService.getById(ownerId); // если нет — 404
        User owner = toUser(ownerDto);

        ItemRequest req = (dto.getRequestId() == null) ? null
                : new ItemRequest(dto.getRequestId(), null, null, null);

        Item item = ItemMapper.toItem(dto, owner, req);
        item.setId(seq.getAndIncrement());
        storage.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        Item item = getOrThrow(itemId);
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new SecurityException("Only owner can edit the item: " + itemId);
        }
        if (patch.getName() != null) item.setName(patch.getName());
        if (patch.getDescription() != null) item.setDescription(patch.getDescription());
        if (patch.getAvailable() != null) item.setAvailable(patch.getAvailable());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        return ItemMapper.toItemDto(getOrThrow(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId) {
        return storage.values().stream()
                .filter(i -> i.getOwner() != null && Objects.equals(i.getOwner().getId(), ownerId))
                .sorted(Comparator.comparing(Item::getId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return List.of();
        String q = text.toLowerCase();
        return storage.values().stream()
                .filter(Item::isAvailable)
                .filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(q))
                        || (i.getDescription() != null && i.getDescription().toLowerCase().contains(q)))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private Item getOrThrow(Long id) {
        Item i = storage.get(id);
        if (i == null) throw new NoSuchElementException("Item not found: " + id);
        return i;
    }

    private static User toUser(UserDto dto) {
        User u = new User();
        u.setId(dto.getId());
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        return u;
    }

    private void validateItemForCreate(ItemDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Item body must not be null");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Item name must not be null or blank");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Item description must not be null or blank");
        }
        if (dto.getAvailable() == null) {
            throw new IllegalArgumentException("Item available flag must not be null");
        }
    }

}