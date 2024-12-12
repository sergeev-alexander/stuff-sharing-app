package alexander.sergeev.stuff_sharing_app.user.item.repository;

import alexander.sergeev.stuff_sharing_app.exception.NotFoundException;
import alexander.sergeev.stuff_sharing_app.item.model.Item;
import alexander.sergeev.stuff_sharing_app.item.repository.ItemRepository;
import alexander.sergeev.stuff_sharing_app.request.model.Request;
import alexander.sergeev.stuff_sharing_app.request.repository.RequestRepository;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import alexander.sergeev.stuff_sharing_app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    RequestRepository requestRepository;

    private final Pageable pageable = PageRequest.of(0, 20);

    private User owner;

    private User requester;

    private Request request;

    private Item item;

    @BeforeEach
    void setUsersAndItemAndRequest() {
        owner = new User(
                null,
                "Owner name",
                "owner@email.com");

        requester = new User(
                null,
                "Requester name",
                "requester@email.com");

        request = new Request(
                null,
                "Request description",
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                requester);

        item = new Item(
                null,
                "Item name",
                "Item description",
                true,
                request,
                owner);
    }

    @Test
    void findByOwnerId_whenItemIsPresent_shouldReturnItemList() {
        userRepository.save(owner);
        userRepository.save(requester);
        requestRepository.save(request);
        itemRepository.save(item);
        List<Item> expected = List.of(item);
        List<Item> result = itemRepository.findByOwnerId(1L);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void findByOwnerId_overloaded_whenItemIsPresent_shouldReturnItemList() {
        userRepository.save(owner);
        userRepository.save(requester);
        requestRepository.save(request);
        itemRepository.save(item);
        List<Item> expected = List.of(item);
        List<Item> result = itemRepository.findByOwnerId(1L, pageable);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void findByOwnerId_whenItemIsNotPresent_shouldReturnAnEmptyList() {
        List<Item> result = itemRepository.findByOwnerId(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByOwnerId_overloaded_whenItemIsNotPresent_shouldReturnAnEmptyList() {
        List<Item> result = itemRepository.findByOwnerId(1L, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchByText_whenItemIsPresent_shouldReturnItemList() {
        userRepository.save(owner);
        item.setRequest(null);
        itemRepository.save(item);
        List<Item> result = itemRepository
                .searchByTextInNameOrDescriptionAndAvailableTrue(
                        "item name", "item name", pageable);
        List<Item> expected = List.of(item);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void searchByText_whenItemIsNotPresent_shouldReturnAnEmptyList() {
        List<Item> result = itemRepository
                .searchByTextInNameOrDescriptionAndAvailableTrue(
                        "item name", "item name", pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdAndOwnerId_whenItemIsPresent_shouldReturnOptionalOfItem() {
        userRepository.save(owner);
        item.setRequest(null);
        itemRepository.save(item);
        Optional<Item> result = itemRepository.findByIdAndOwnerId(1L, 1L);
        assertTrue(result.isPresent());
        assertEquals(item, result.get());
    }

    @Test
    void findByIdAndOwnerId_whenItemIsNotPresent_shouldReturnAnEmptyOptional() {
        Optional<Item> result = itemRepository.findByIdAndOwnerId(1L, 1L);
        assertFalse(result.isPresent());
    }

    @Test
    void findByRequestIdIn_whenItemIsPresent_shouldReturnItemList() {
        userRepository.save(owner);
        userRepository.save(requester);
        requestRepository.save(request);
        itemRepository.save(item);
        List<Item> expected = List.of(item);
        List<Item> result = itemRepository.findByRequestIdIn(List.of(1L));
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void findByRequestIdIn_whenItemIsNotPresent_shouldReturnAnEmptyList() {
        List<Item> result = itemRepository
                .findByRequestIdIn(List.of(1L));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByRequestId_whenItemIsPresent_shouldReturnItemList() {
        userRepository.save(owner);
        userRepository.save(requester);
        requestRepository.save(request);
        itemRepository.save(item);
        List<Item> result = itemRepository.findByRequestId(1L);
        List<Item> expected = List.of(item);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void findByRequestId_whenItemIsNotPresent_shouldReturnAnEmptyList() {
        List<Item> result = itemRepository.findByRequestId(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteByOwnerId_whenItemIsPresent_shouldDeleteIt() {
        userRepository.save(owner);
        item.setRequest(null);
        itemRepository.save(item);
        assertEquals(List.of(item), itemRepository.findByOwnerId(1L));
        itemRepository.deleteByOwnerId(1L);
        assertNotNull(itemRepository.findByOwnerId(1L));
        assertTrue(itemRepository.findByOwnerId(1L).isEmpty());
    }

    @Test
    void getItemById_whenItemIsPresent_shouldReturnIt() {
        userRepository.save(owner);
        item.setRequest(null);
        itemRepository.save(item);
        Item result = itemRepository.getItemById(1L);
        assertNotNull(result);
        assertEquals(item, result);
    }

    @Test
    void getItemById_whenItemIsNotPresent_shouldThrowNotFoundException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRepository.getItemById(1L));
        assertEquals("There's no item with id 1", notFoundException.getMessage());
    }
}