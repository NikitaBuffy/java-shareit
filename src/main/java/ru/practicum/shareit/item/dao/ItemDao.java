package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemDao {

    Item addItem(Item item);

    Item editItem(Long itemId, Item item);

    Item getItemById(Long itemId);

    List<Item> getItems(Long userId, User owner);

    List<Item> searchItems(String text);
}
