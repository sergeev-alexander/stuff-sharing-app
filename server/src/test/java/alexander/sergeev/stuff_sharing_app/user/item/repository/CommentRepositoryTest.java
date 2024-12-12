package alexander.sergeev.stuff_sharing_app.user.item.repository;

import alexander.sergeev.stuff_sharing_app.comment.model.Comment;
import alexander.sergeev.stuff_sharing_app.item.model.Item;
import alexander.sergeev.stuff_sharing_app.item.repository.CommentRepository;
import alexander.sergeev.stuff_sharing_app.item.repository.ItemRepository;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import alexander.sergeev.stuff_sharing_app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;

    private User author;

    private Item item;

    private Comment comment;

    @BeforeEach
    void setUsersAndItemAndComment() {
        user = new User(
                null,
                "User name",
                "user@email.com");

        author = new User(
                null,
                "Author name",
                "author@email.com");

        item = new Item(
                null,
                "Item name",
                "Item description",
                true,
                null,
                user);

        comment = new Comment(
                null,
                "Comment text",
                item,
                author,
                LocalDateTime.of(2000,1,1,1,1,1));
    }

    @Test
    void findByItemId_whenCommentPresent_shouldReturnCommentList() {
        userRepository.save(user);
        userRepository.save(author);
        itemRepository.save(item);
        commentRepository.save(comment);
        List<Comment> result = commentRepository.findByItemId(1L);
        List<Comment> expected = List.of(comment);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void findByItemId_whenCommentNotPresent_shouldReturnAnEmptyCommentList() {
        List<Comment> result = commentRepository.findByItemId(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemIdIn_whenCommentPresent_shouldReturnCommentList() {
        userRepository.save(user);
        userRepository.save(author);
        itemRepository.save(item);
        commentRepository.save(comment);
        List<Comment> result = commentRepository.findByItemIdIn(List.of(1L));
        List<Comment> expected = List.of(comment);
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void findByItemIdIn_whenCommentNotPresent_shouldReturnAnEmptyCommentList() {
        List<Comment> result = commentRepository.findByItemIdIn(List.of(1L));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}