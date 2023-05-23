package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestSort;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest request = ItemRequestMapper.dtoToRequest(itemRequestDto);
        request.setRequestor(userRepository.getExistingUser(userId));
        request.setCreated(LocalDateTime.now());
        requestRepository.save(request);
        log.info("Оставлен запрос на вещь от пользователя с ID: {} - {}", userId, request);
        return ItemRequestMapper.requestToDto(request);
    }

    @Override
    public List<ItemRequestDto> getRequests(Long userId) {
        userRepository.getExistingUser(userId);
        List<ItemRequestDto> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::requestToDto)
                .collect(Collectors.toList());

        requests.forEach(this::findItemsForRequest);
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size, ItemRequestSort sort) {
        Pageable page = createPageRequest(from, size, sort);
        List<ItemRequestDto> requests = requestRepository.findAllOtherRequests(userId, page)
                .getContent()
                .stream()
                .map(ItemRequestMapper::requestToDto)
                .collect(Collectors.toList());

        requests.forEach(this::findItemsForRequest);
        return requests;

    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.getExistingUser(userId);
        ItemRequest itemRequest = requestRepository.getExistingRequest(requestId);
        ItemRequestDto requestDto = ItemRequestMapper.requestToDto(itemRequest);
        findItemsForRequest(requestDto);
        return requestDto;
    }

    private void findItemsForRequest(ItemRequestDto requestDto) {
        List<ItemDto> items = itemRepository.findByRequestId(requestDto.getId())
                .stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            requestDto.setItems(new ArrayList<>());
        } else {
            requestDto.setItems(items);
        }
    }

    private PageRequest createPageRequest(int from, int size, ItemRequestSort sort) {
        return PageRequest.of(from / size, size, sort.getSortValue());
    }
}
