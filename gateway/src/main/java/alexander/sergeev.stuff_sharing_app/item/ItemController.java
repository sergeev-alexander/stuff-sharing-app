package alexander.sergeev.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {

    private final alexander.sergeev.item.ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllOwnerItems(
            HttpServletRequest request,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size,
            @RequestHeader(header) @Positive Long ownerId) {
        log.info("Id-{} {} {}?{}", ownerId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemClient.getAllOwnerItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            HttpServletRequest request,
            @RequestHeader(header) @Positive Long ownerId,
            @PathVariable @Positive Long itemId) {
        log.info("Id-{} {} {}", ownerId, request.getMethod(), request.getRequestURI());
        return itemClient.getItemById(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(
            HttpServletRequest request,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size,
            @RequestHeader(header) @Positive Long userId,
            @RequestParam(value = "text") String text) {
        log.info("Id-{} {} {}?{}", userId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (text.isBlank()) return new ResponseEntity<>(Collections.EMPTY_LIST, HttpStatus.OK);
        return itemClient.getItemsBySearch(from, size, userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(
            HttpServletRequest request,
            @RequestHeader(header) @Positive Long ownerId,
            @RequestBody @Validated(ValidationMarker.OnCreate.class) IncomingItemDto incomingItemDto) {
        log.info("Id-{} {} {} {}", ownerId, request.getMethod(), request.getRequestURI(), incomingItemDto);
        return itemClient.postItem(ownerId, incomingItemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            HttpServletRequest request,
            @RequestHeader(header) @Positive Long authorId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Validated(ValidationMarker.OnCreate.class) IncomingCommentDto incomingCommentDto) {
        log.info("Id-{} {} {} {}", authorId, request.getMethod(), request.getRequestURI(), incomingCommentDto);
        return itemClient.postComment(authorId, itemId, incomingCommentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItemById(
            HttpServletRequest request,
            @RequestHeader(header) @Positive Long ownerId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Validated(ValidationMarker.OnUpdate.class) IncomingItemDto incomingItemDto) {
        log.info("Id-{} {} {} {}", ownerId, request.getMethod(), request.getRequestURI(), incomingItemDto);
        return itemClient.patchItemById(ownerId, itemId, incomingItemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemById(
            HttpServletRequest request,
            @RequestHeader(header) @Positive Long ownerId,
            @PathVariable @Positive Long itemId) {
        log.info("Id-{} {} {}", ownerId, request.getMethod(), request.getRequestURI());
        return itemClient.deleteItemById(ownerId, itemId);
    }

}

