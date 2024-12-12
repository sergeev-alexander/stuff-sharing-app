package alexander.sergeev.stuff_sharing_app.user.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUser() {
        user = new User(
                null,
                "Some name",
                "some@email.com");
    }

    @Test
    void findBy_whenUsersAreNotPresent_shouldReturnEmptyCollection() {
        assertEquals(List.of(), userRepository.findAll());
    }

    @Test
    void findBy_whenUsersArePresent_shouldReturnCollectionOfUsers() {
        userRepository.save(user);
        List<User> result = userRepository.findAll();
        assertEquals(List.of(user), result);
    }

    @Test
    void checkUserById_whenUserNotExists_shouldThrowNotFoundException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userRepository.checkUserById(1L));
        assertEquals("There's no user with id 1", notFoundException.getMessage());
    }

    @Test
    void checkUserById_whenUserExists_shouldNotThrowNotFoundException() {
        userRepository.save(user);
        assertDoesNotThrow(() -> userRepository.checkUserById(1L));
    }

    @Test
    void getUserById_whenUserNotExists_shouldThrowNotFoundException() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userRepository.checkUserById(1L));
        assertEquals("There's no user with id 1", notFoundException.getMessage());
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() {
        userRepository.save(user);
        User result = userRepository.getUserById(1L);
        assertEquals(user, result);
    }

}
