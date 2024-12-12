package alexander.sergeev.stuff_sharing_app.user.request.repository;

import alexander.sergeev.stuff_sharing_app.exception.NotFoundException;
import alexander.sergeev.stuff_sharing_app.request.model.Request;
import alexander.sergeev.stuff_sharing_app.request.repository.RequestRepository;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import alexander.sergeev.stuff_sharing_app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private Request request;

    private User user;

    private final Sort sortByCreatingDesc = Sort.by(Sort.Direction.DESC, "created");

    @BeforeEach
    void setUserAndRequest() {

        user = new User(null,
                "Some name",
                "Some description");

        request = new Request(
                null,
                "Some description",
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                user);
    }

    @Test
    void findByRequesterId_whenRequestIsPresent_shouldReturnListOfRequests() {
        userRepository.save(user);
        requestRepository.save(request);
        List<Request> result = requestRepository.findByRequesterId(1L,
                PageRequest.of(0, 20, sortByCreatingDesc));
        assertNotNull(result);
        assertEquals(List.of(request), result);
    }

    @Test
    void findByRequesterId_whenRequestIsNotPresent_shouldReturnAnEmptyList() {
        List<Request> result = requestRepository.findByRequesterId(1L,
                PageRequest.of(0, 20, sortByCreatingDesc));
        assertNotNull(result);
        assertEquals(List.of(), result);
    }

    @Test
    void findByRequesterIdIsNot_whenRequestIsPresent_shouldReturnListOfRequests() {
        userRepository.save(user);
        requestRepository.save(request);
        User user2 = new User(null,
                "Some name 2",
                "Some description 2");
        Request request2 = new Request(
                null,
                "Some description 2",
                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                user2);
        userRepository.save(user2);
        requestRepository.save(request2);
        List<Request> result = requestRepository.findByRequesterIdIsNot(1L,
                PageRequest.of(0, 20, sortByCreatingDesc));
        assertNotNull(result);
        assertEquals(List.of(request2), result);
    }

    @Test
    void findByRequesterIdIsNot_whenRequestIsNotPresent_shouldReturnAnEmptyList() {
        List<Request> result = requestRepository.findByRequesterId(1L,
                PageRequest.of(0, 20, sortByCreatingDesc));
        assertNotNull(result);
        assertEquals(List.of(), result);
    }

    @Test
    void findRequestById_whenRequestIsPresent_shouldReturnIt() {
        userRepository.save(user);
        requestRepository.save(request);
        Request result = requestRepository.findRequestById(1L);
        assertNotNull(result);
        assertEquals(request, result);
    }

    @Test
    void findRequestById_whenRequestIsNotPresent_shouldThrowNotFoundException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> requestRepository.findRequestById(1L));
        assertEquals("There's no request with id 1",
                notFoundException.getMessage());
    }
}