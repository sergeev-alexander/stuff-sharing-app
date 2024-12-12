package alexander.sergeev.stuff_sharing_app.comment.dto;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public OutgoingCommentDto mapCommentToOutgoingDto(Comment comment) {
        return new OutgoingCommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment mapIncommingDtoToComment(IncomingCommentDto incomingCommentDto) {
        return new Comment(
                null,
                incomingCommentDto.getText(),
                null,
                null,
                LocalDateTime.now());
    }

}
