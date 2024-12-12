package alexander.sergeev.stuff_sharing_app.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OutgoingCommentDto {

    private Long id;

    private String text;

    private String authorName;

    private LocalDateTime created;

}