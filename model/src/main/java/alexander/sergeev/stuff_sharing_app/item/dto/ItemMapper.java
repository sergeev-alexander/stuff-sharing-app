package alexander.sergeev.stuff_sharing_app.item.dto;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public OutgoingItemDto mapItemToOutgoingDto(Item item) {
        return new OutgoingItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                List.of(),
                item.getRequest() == null ? null : item.getRequest().getId());
    }

    public Item mapIncomingDtoToItem(IncomingItemDto incomingItemDto) {
        return new Item(
                null,
                incomingItemDto.getName(),
                incomingItemDto.getDescription(),
                incomingItemDto.getAvailable(),
                null,
                null);
    }

}
