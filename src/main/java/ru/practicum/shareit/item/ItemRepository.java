package ru.practicum.shareit.item;

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
    List<Item> findItemsByAvailabilityAndNameOrDesc(@Param("searchTxt") String searchTxt);

    List<Item> findByOwnerId(Integer ownerId);
}
