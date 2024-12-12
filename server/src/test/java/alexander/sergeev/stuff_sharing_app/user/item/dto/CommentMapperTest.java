package alexander.sergeev.stuff_sharing_app.user.item.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CommentMapperTest {

    @Test
    void mapCommentToOutgoingDto() {
        Comment comment = new Comment(
                1L,
                "Comment text",
                null,
                new User(
                        2L,
                        "Author name",
                        "author@email.com"),
                LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        OutgoingCommentDto result = CommentMapper.mapCommentToOutgoingDto(comment);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getCreated(), result.getCreated());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
    }

    @Test
    void mapIncomingDtoToComment() {
        IncomingCommentDto incomingCommentDto = new IncomingCommentDto(
                null,
                "Comment text");
        Comment result = CommentMapper.mapIncommingDtoToComment(incomingCommentDto);
        assertEquals(incomingCommentDto.getText(), result.getText());
    }

}
