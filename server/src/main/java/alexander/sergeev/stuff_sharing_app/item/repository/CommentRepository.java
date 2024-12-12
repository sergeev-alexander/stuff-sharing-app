package alexander.sergeev.stuff_sharing_app.item.repository;

import alexander.sergeev.stuff_sharing_app.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(Long itemId);

    List<Comment> findByItemIdIn(Collection<Long> itemIdList);

}