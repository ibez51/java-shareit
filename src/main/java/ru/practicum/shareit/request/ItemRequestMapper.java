package ru.practicum.shareit.request;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {
    @Mapping(source = "itemRequest.id", target = "id")
    ItemRequestCreateDto toDto(ItemRequest itemRequest, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user", target = "requestor")
    ItemRequest toItemRequest(ItemRequestCreateDto itemRequestCreateDto, User user);

    @Mapping(source = "itemList", target = "items")
    ItemRequestOutputDto toOutputDto(ItemRequest itemRequest, List<Item> itemList);
}
