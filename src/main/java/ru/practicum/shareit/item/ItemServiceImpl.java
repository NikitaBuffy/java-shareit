package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto addItem(int userId, ItemDto itemDto) {
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwner(userDao.getUserById(userId));
        return ItemMapper.itemToDto(itemDao.addItem(item));
    }

    @Override
    public ItemDto editItem(int userId, int itemId, ItemDto itemDto) {
        Item item = itemDao.getItemById(itemId);
        if (!item.getOwner().equals(userDao.getUserById(userId))) {
            log.warn("Пользователь с ID: " + userId + " не является владельцом вещи: " + item);
            throw new NotOwnerException("Редактировать вещь может только её владелец!");
        }
        Item updateItem = ItemMapper.dtoToItem(itemDto);
        return ItemMapper.itemToDto(itemDao.editItem(itemId, updateItem));
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return ItemMapper.itemToDto(itemDao.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItems(int userId) {
        User owner = userDao.getUserById(userId);
        List<Item> itemList = itemDao.getItems(userId, owner);
        return itemList.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> itemList = itemDao.searchItems(text);
        return itemList.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }
}
