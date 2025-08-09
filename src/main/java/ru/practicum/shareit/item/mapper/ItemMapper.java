package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

public final class ItemMapper {
    private ItemMapper() {}

    public static ItemDto toItemDto(Item item) {
        if (item == null) return null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    // Для создания/обновления Item из DTO
    public static Item toItem(ItemDto dto, User owner, ItemRequest request) {
        if (dto == null) return null;
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : false);
        item.setOwner(owner);
        item.setRequest(request); // может быть null
        return item;
    }
}