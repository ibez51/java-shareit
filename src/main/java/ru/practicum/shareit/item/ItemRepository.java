package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query(value = "select item from Item item " +
            "where item.available = true " +
            "and (lower(item.name) like lower(concat('%', :searchTxt, '%')) " +
            "or lower(item.description) like lower(concat('%', :searchTxt, '%')))")
    Page<Item> findItemsByAvailabilityAndNameOrDesc(@Param("searchTxt") String searchTxt, Pageable page);

    Page<Item> findByOwnerIdOrderByIdAsc(Integer ownerId, Pageable page);

    List<Item> findByRequestIdIn(List<Integer> itemRequestIds);
}
