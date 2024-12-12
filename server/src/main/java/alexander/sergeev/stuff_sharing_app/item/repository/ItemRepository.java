package alexander.sergeev.stuff_sharing_app.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId, Pageable pageable);

    List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT i " +
            "FROM Item as i " +
            "WHERE i.available=true " +
            "AND " +
            "(" +
            "LOWER(i.name) LIKE %:text% " +
            "OR " +
            "LOWER(i.description) LIKE %:sameText%" +
            ")")
    List<Item> searchByTextInNameOrDescriptionAndAvailableTrue(String text, String sameText, Pageable pageable);

    Optional<Item> findByIdAndOwnerId(Long itemId, Long ownerId);

    List<Item> findByRequestIdIn(List<Long> requestIds);

    List<Item> findByRequestId(Long requestId);

    void deleteByOwnerId(Long ownerId);

    default Item getItemById(Long itemId) {
        return findById(itemId).orElseThrow(() -> new NotFoundException("There's no item with id " + itemId));
    }

}
