package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService items;

    public ItemController(ItemService items) {
        this.items = items;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @RequestBody ItemDto dto) {
        return items.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto patch) {
        return items.update(ownerId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                           @PathVariable Long itemId) {
        return items.getById(requesterId, itemId);
    }

    @GetMapping
    public List<ItemDto> ownerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return items.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return items.search(text);
    }
}