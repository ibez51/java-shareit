package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public List<ItemRequestOutputDto> getItemRequests(Integer userId) {
        userService.getUser(userId);

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId);

        List<Integer> itemRequestIds = itemRequestList.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> itemsList = itemService.getItemsByRequestIds(itemRequestIds);

        return itemRequestList.stream()
                .map(itemRequest -> itemRequestMapper.toOutputDto(itemRequest, itemsList.stream()
                        .filter(item -> item.getRequestId() == itemRequest.getId())
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestOutputDto getItemRequest(Integer userId,
                                               Integer itemRequestId) {
        userService.getUser(userId);

        ItemRequest itemRequest = getItemRequest(itemRequestId);
        List<Item> itemsList = itemService.getItemsByRequestIds(List.of(itemRequest.getId()));

        return itemRequestMapper.toOutputDto(itemRequest, itemsList);
    }

    @Override
    public List<ItemRequestOutputDto> getAllItemRequests(Integer userId,
                                                         Integer from,
                                                         Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<ItemRequest> pageItems = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedAsc(userId, page);

        List<Integer> itemRequestIds = pageItems.map(ItemRequest::getId).getContent();

        List<Item> itemsList = itemService.getItemsByRequestIds(itemRequestIds);

        return pageItems.map(x -> itemRequestMapper.toOutputDto(x, itemsList.stream()
                        .filter(item -> item.getRequestId() == x.getId())
                        .collect(Collectors.toList())))
                .getContent();
    }

    @Override
    @Transactional
    public ItemRequestOutputDto addItemRequest(Integer userId,
                                               ItemRequestCreateDto itemRequestCreateDto) {
        User user = userService.getUser(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestCreateDto, user);

        itemRequest = itemRequestRepository.save(itemRequest);

        return itemRequestMapper.toOutputDto(itemRequest, itemService.getItemsByRequestIds(List.of(itemRequest.getId())));
    }

    @Override
    public ItemRequest getItemRequest(Integer itemRequestId) {
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NullPointerException("Запрос с Id " + itemRequestId + " не найден."));
    }
}
