package alexander.sergeev.stuff_sharing_app.item.service;

import alexander.sergeev.stuff_sharing_app.comment.dto.IncomingCommentDto;
import alexander.sergeev.stuff_sharing_app.comment.dto.OutgoingCommentDto;
import alexander.sergeev.stuff_sharing_app.item.dto.IncomingItemDto;
import alexander.sergeev.stuff_sharing_app.item.dto.OutgoingItemDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {

    List<OutgoingItemDto> getAllOwnerItems(Long ownerId, Pageable pageable);

    OutgoingItemDto getItemDtoById(Long userId, Long itemId);

    List<OutgoingItemDto> getItemsBySearch(Long userId, String text, Pageable pageable);

    OutgoingItemDto postItem(Long ownerId, IncomingItemDto incomingItemDto);

    OutgoingCommentDto postComment(Long authorId, Long itemId, IncomingCommentDto incomingCommentDto);

    OutgoingItemDto patchItemById(Long ownerId, Long itemId, IncomingItemDto incomingItemDto);

    OutgoingItemDto deleteItemById(Long ownerId, Long itemId);

    void deleteAllOwnerItems(Long ownerId);

}