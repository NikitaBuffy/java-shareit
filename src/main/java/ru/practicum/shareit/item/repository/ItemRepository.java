package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByOwnerId(int userId);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name, String description);

    default Item getExistingItem(int itemId) {
        return findById(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException("Предмет с ID: " + itemId + " не существует.");
        });
    }
}
