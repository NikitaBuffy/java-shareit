package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long userId);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name, String description);

    default Item getExistingItem(Long itemId) {
        return findById(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException("Предмет с ID: " + itemId + " не существует.");
        });
    }
}
