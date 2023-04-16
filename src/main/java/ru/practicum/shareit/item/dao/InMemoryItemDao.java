package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@Primary
public class InMemoryItemDao implements ItemDao {

    private final HashMap<Integer, Item> itemMap = new HashMap<>();
    int generatedId = 0;

    @Override
    public Item addItem(Item item) {
        item.setId(generateId());
        itemMap.put(item.getId(), item);
        log.info("Добавлен предмет с ID: {} - {}", item.getId(), item);
        return item;
    }

    @Override
    public Item editItem(int itemId, Item item) {
        if (itemMap.containsKey(itemId)) {
            Item mainItem = itemMap.get(itemId);

            if (item.getName() != null) {
                mainItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                mainItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                mainItem.setAvailable(item.getAvailable());
            }

            log.info("Обновлен предмет с ID: {}. Новые данные: {}", itemId, mainItem);
        } else {
            log.warn("Не найден предмет при попытке обновления.");
            throw new UserNotFoundException("Предмет с ID: " + itemId + " не существует.");
        }
        return itemMap.get(itemId);
    }

    @Override
    public Item getItemById(int itemId) {
        if (itemMap.containsKey(itemId)) {
            return itemMap.get(itemId);
        } else {
            log.warn("Не найден предмет при поиске по ID.");
            throw new UserNotFoundException("Предмет с ID: " + itemId + " не существует.");
        }
    }

    @Override
    public List<Item> getItems(int userId, User owner) {
        List<Item> itemList = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : itemMap.entrySet()) {
            if (entry.getValue().getOwner().equals(owner)) {
                itemList.add(entry.getValue());
            }
        }
        return itemList;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> itemsFound = new ArrayList<>();
        for (Map.Entry<Integer, Item> entry : itemMap.entrySet()) {
            Item item = entry.getValue();
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()) {
                itemsFound.add(item);
            }
        }
        return itemsFound;
    }

    private int generateId() {
        return ++generatedId;
    }
}
