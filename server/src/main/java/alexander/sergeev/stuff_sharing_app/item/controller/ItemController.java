package alexander.sergeev.stuff_sharing_app.item.controller;

import alexander.sergeev.stuff_sharing_app.comment.dto.IncomingCommentDto;
import alexander.sergeev.stuff_sharing_app.comment.dto.OutgoingCommentDto;
import alexander.sergeev.stuff_sharing_app.item.dto.IncomingItemDto;
import alexander.sergeev.stuff_sharing_app.item.dto.OutgoingItemDto;
import alexander.sergeev.stuff_sharing_app.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static alexander.sergeev.stuff_sharing_app.http.HttpHeader.header;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");

    private final ItemService itemService;

    @GetMapping
    public Collection<OutgoingItemDto> getAllOwnerItems(
            HttpServletRequest request,
            @RequestParam(value = "from") Integer firstElement,
            @RequestParam(value = "size") Integer size,
            @RequestHeader(header) Long ownerId) {
        log.info("Id-{} {} {}?{}", ownerId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.getAllOwnerItems(ownerId, PageRequest.of(firstElement / size, size, sortByStartDesc));
    }

    @GetMapping("/{itemId}")
    public OutgoingItemDto getItemById(
            HttpServletRequest request,
            @RequestHeader(header) Long ownerId,
            @PathVariable Long itemId) {
        log.info("Id-{} {} {}", ownerId, request.getMethod(), request.getRequestURI());
        return itemService.getItemDtoById(ownerId, itemId);
    }

    @GetMapping("/search")
    public Collection<OutgoingItemDto> getItemsBySearch(
            HttpServletRequest request,
            @RequestParam(value = "from") Integer firstElement,
            @RequestParam(value = "size") Integer size,
            @RequestHeader(header) Long userId,
            @RequestParam(value = "text") String text) {
        log.info("Id-{} {} {}?{}", userId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemService.getItemsBySearch(userId, text, PageRequest.of(firstElement / size, size));
    }

    @PostMapping
    public OutgoingItemDto postItem(
            HttpServletRequest request,
            @RequestHeader(header) Long ownerId,
            @RequestBody IncomingItemDto incomingItemDto) {
        log.info("Id-{} {} {} {}", ownerId, request.getMethod(), request.getRequestURI(), incomingItemDto);
        return itemService.postItem(ownerId, incomingItemDto);
    }

    @PostMapping("/{itemId}/comment")
    public OutgoingCommentDto postComment(
            HttpServletRequest request,
            @RequestHeader(header) Long authorId,
            @PathVariable Long itemId,
            @RequestBody IncomingCommentDto incomingCommentDto) {
        log.info("Id-{} {} {} {}", authorId, request.getMethod(), request.getRequestURI(), incomingCommentDto);
        return itemService.postComment(authorId, itemId, incomingCommentDto);
    }

    @PatchMapping("/{itemId}")
    public OutgoingItemDto patchItemById(
            HttpServletRequest request,
            @RequestHeader(header) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody IncomingItemDto incomingItemDto) {
        log.info("Id-{} {} {} {}", ownerId, request.getMethod(), request.getRequestURI(), incomingItemDto);
        return itemService.patchItemById(ownerId, itemId, incomingItemDto);
    }

    @DeleteMapping("/{itemId}")
    public OutgoingItemDto deleteItemById(
            HttpServletRequest request,
            @RequestHeader(header) Long ownerId,
            @PathVariable Long itemId) {
        log.info("Id-{} {} {}", ownerId, request.getMethod(), request.getRequestURI());
        return itemService.deleteItemById(ownerId, itemId);
    }
}