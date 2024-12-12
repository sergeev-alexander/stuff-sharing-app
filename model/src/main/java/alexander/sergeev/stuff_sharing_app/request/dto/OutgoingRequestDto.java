package alexander.sergeev.stuff_sharing_app.request.dto;

import alexander.sergeev.stuff_sharing_app.item.dto.OutgoingItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutgoingRequestDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private Long requesterId;

    private List<OutgoingItemDto> items;

}