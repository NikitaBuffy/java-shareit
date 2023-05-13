package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwnerId(Long userId, Pageable pageable);

    Page<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name, String description, Pageable pageable);

    List<Item> findByRequestId(Long requestId);

    default Item getExistingItem(Long itemId) {
        return findById(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException("Предмет с ID: " + itemId + " не существует.");
        });
    }
}
