package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long userId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id != :userId")
    Page<ItemRequest> findAllOtherRequests(Long userId, Pageable pageable);

    default ItemRequest getExistingRequest(Long requestId) {
        return findById(requestId).orElseThrow(() -> {
            throw new RequestNotFoundException("Запрос с ID: " + requestId + " не существует.");
        });
    }
}
