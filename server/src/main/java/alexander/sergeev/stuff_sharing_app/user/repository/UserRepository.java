package alexander.sergeev.stuff_sharing_app.user.repository;

import alexander.sergeev.stuff_sharing_app.exception.NotFoundException;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findBy(Pageable pageable);

    default void checkUserById(Long userId) {
        if (!existsById(userId))
            throw new NotFoundException("There's no user with id " + userId);
    }

    default User getUserById(Long userId) {
        return findById(userId)
                .orElseThrow(() -> new NotFoundException("There's no user with id " + userId));
    }
}
