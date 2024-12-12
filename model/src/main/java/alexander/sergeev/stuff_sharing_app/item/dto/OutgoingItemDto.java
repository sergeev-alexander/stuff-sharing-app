package alexander.sergeev.stuff_sharing_app.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OutgoingItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private LastNextBookingDto lastBooking;

    private LastNextBookingDto nextBooking;

    private List<OutgoingCommentDto> comments;

    private Long requestId;

}
