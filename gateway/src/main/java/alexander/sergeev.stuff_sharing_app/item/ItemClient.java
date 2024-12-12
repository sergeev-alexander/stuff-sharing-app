package alexander.sergeev.stuff_sharing_app.item;

import alexander.sergeev.stuff_sharing_app.client.BaseClient;
import alexander.sergeev.stuff_sharing_app.comment.dto.IncomingCommentDto;
import alexander.sergeev.stuff_sharing_app.item.dto.IncomingItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${stuff_sharing_app_server_url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getAllOwnerItems(Long ownerId, Integer from, Integer size) {
        return get("?from={from}&size={size}", ownerId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getItemById(Long ownerId, Long itemId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getItemsBySearch(Integer from, Integer size, Long userId, String text) {
        return get("/search?from={from}&size={size}&text={text}", userId,
                Map.of("from", from, "size", size, "text", text));
    }

    public ResponseEntity<Object> postItem(Long ownerId, IncomingItemDto incomingItemDto) {
        return post("", ownerId, incomingItemDto);
    }

    public ResponseEntity<Object> postComment(Long authorId, Long itemId, IncomingCommentDto incomingCommentDto) {
        return post("/" + itemId + "/comment", authorId, incomingCommentDto);
    }

    public ResponseEntity<Object> patchItemById(Long ownerId, Long itemId, IncomingItemDto incomingItemDto) {
        return patch("/" + itemId, ownerId, incomingItemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemById(Long ownerId, Long itemId) {
        return delete("/" + itemId, ownerId);
    }
}