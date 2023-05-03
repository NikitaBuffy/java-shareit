package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemDao {

    Item addItem(Item item);

    Item editItem(int itemId, Item item);

    Item getItemById(int itemId);

    List<Item> getItems(int userId, User owner);

    List<Item> searchItems(String text);
}
