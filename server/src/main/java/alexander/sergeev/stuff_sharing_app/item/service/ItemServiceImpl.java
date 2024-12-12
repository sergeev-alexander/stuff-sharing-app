package alexander.sergeev.stuff_sharing_app.item.service;

import alexander.sergeev.stuff_sharing_app.booking.dto.BookingMapper;
import alexander.sergeev.stuff_sharing_app.booking.dto.LastNextBookingDto;
import alexander.sergeev.stuff_sharing_app.booking.model.Booking;
import alexander.sergeev.stuff_sharing_app.booking.model.BookingStatus;
import alexander.sergeev.stuff_sharing_app.booking.repository.BookingRepository;
import alexander.sergeev.stuff_sharing_app.comment.dto.CommentMapper;
import alexander.sergeev.stuff_sharing_app.comment.dto.IncomingCommentDto;
import alexander.sergeev.stuff_sharing_app.comment.dto.OutgoingCommentDto;
import alexander.sergeev.stuff_sharing_app.comment.model.Comment;
import alexander.sergeev.stuff_sharing_app.exception.NotAvailableItemException;
import alexander.sergeev.stuff_sharing_app.exception.NotFoundException;
import alexander.sergeev.stuff_sharing_app.item.dto.IncomingItemDto;
import alexander.sergeev.stuff_sharing_app.item.dto.ItemMapper;
import alexander.sergeev.stuff_sharing_app.item.dto.OutgoingItemDto;
import alexander.sergeev.stuff_sharing_app.item.model.Item;
import alexander.sergeev.stuff_sharing_app.item.repository.CommentRepository;
import alexander.sergeev.stuff_sharing_app.item.repository.ItemRepository;
import alexander.sergeev.stuff_sharing_app.request.repository.RequestRepository;
import alexander.sergeev.stuff_sharing_app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final RequestRepository requestRepository;

    @Override
    public List<OutgoingItemDto> getAllOwnerItems(Long ownerId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        userRepository.checkUserById(ownerId);
        List<OutgoingItemDto> outgoingItemDtoList = itemRepository.findByOwnerId(ownerId,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()))
                .stream()
                .map(ItemMapper::mapItemToOutgoingDto)
                .collect(toList());
        List<Long> itemIdList = outgoingItemDtoList
                .stream()
                .map(OutgoingItemDto::getId)
                .collect(toList());
        Map<Long, List<Comment>> commentMap = commentRepository.findByItemIdIn(itemIdList)
                .stream()
                .collect(groupingBy(comment -> comment.getItem().getId(), toList()));
        Map<Long, List<Booking>> bookingMap = bookingRepository
                .findByItemIdIn(itemIdList, pageable)
                .stream()
                .collect(groupingBy(booking -> booking.getItem().getId(), toList()));
        return outgoingItemDtoList
                .stream()
                .peek(outgoingItemDto -> outgoingItemDto
                        .setComments(commentMap.getOrDefault(outgoingItemDto.getId(), List.of())
                                .stream()
                                .map(CommentMapper::mapCommentToOutgoingDto)
                                .collect(toList())))
                .peek(outgoingItemDto -> outgoingItemDto
                        .setLastBooking(bookingMap.getOrDefault(outgoingItemDto.getId(), List.of())
                                .stream()
                                .filter(booking -> booking.getStart().isBefore(now))
                                .map(BookingMapper::mapBookingToLastNextDto)
                                .reduce((booking1, booking2) -> booking2)
                                .orElse(null)))
                .peek(outgoingItemDto -> outgoingItemDto
                        .setNextBooking(bookingMap.getOrDefault(outgoingItemDto.getId(), List.of())
                                .stream()
                                .filter(booking -> booking.getStart().isAfter(now))
                                .map(BookingMapper::mapBookingToLastNextDto)
                                .reduce((booking1, booking2) -> booking2)
                                .orElse(null)))
                .sorted(Comparator.comparing(OutgoingItemDto::getId))
                .collect(toList());
    }

    @Override
    public OutgoingItemDto getItemDtoById(Long userId, Long itemId) {
        userRepository.checkUserById(userId);
        Item item = itemRepository.getItemById(itemId);
        OutgoingItemDto outgoingItemDto = ItemMapper.mapItemToOutgoingDto(item);
        if (item.getOwner().getId().equals(userId)) {
            outgoingItemDto.setLastBooking(getLastBookingByItemId(itemId));
            outgoingItemDto.setNextBooking(getNextBookingByItemId(itemId));
        }
        outgoingItemDto.setComments(getCommentsByItemId(itemId));
        return outgoingItemDto;
    }

    @Override
    public List<OutgoingItemDto> getItemsBySearch(Long userId, String text, Pageable pageable) {
        userRepository.checkUserById(userId);
        List<Item> itemList =
                itemRepository.searchByTextInNameOrDescriptionAndAvailableTrue(
                        text.toLowerCase(), text.toLowerCase(), pageable);
        Map<Long, List<Comment>> commentMap = commentRepository.findByItemIdIn(itemList
                        .stream()
                        .map(Item::getId)
                        .collect(toList()))
                .stream()
                .collect(groupingBy(comment -> comment.getItem().getId(), toList()));
        return itemList
                .stream()
                .map(ItemMapper::mapItemToOutgoingDto)
                .peek(outgoingItemDto -> outgoingItemDto
                        .setComments(commentMap.getOrDefault(outgoingItemDto.getId(), List.of())
                                .stream()
                                .map(CommentMapper::mapCommentToOutgoingDto)
                                .collect(toList())))
                .collect(toList());
    }

    @Override
    public OutgoingItemDto postItem(Long ownerId, IncomingItemDto incomingItemDto) {
        Item item = ItemMapper.mapIncomingDtoToItem(incomingItemDto);
        item.setOwner(userRepository.getUserById(ownerId));
        if (incomingItemDto.getRequestId() != null) {
            item.setRequest(requestRepository.findRequestById(incomingItemDto.getRequestId()));
        }
        return ItemMapper.mapItemToOutgoingDto(itemRepository.save(item));
    }

    @Override
    public OutgoingCommentDto postComment(Long authorId, Long itemId, IncomingCommentDto incomingCommentDto) {
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(authorId,
                itemId, LocalDateTime.now(), BookingStatus.APPROVED)) {
            throw new NotAvailableItemException("Author with id " + authorId
                    + " has no rights to leve a comment to item with id " + itemId + "!");
        }
        Comment comment = CommentMapper.mapIncommingDtoToComment(incomingCommentDto);
        comment.setAuthor(userRepository.getUserById(authorId));
        comment.setItem(itemRepository.getItemById(itemId));
        return CommentMapper.mapCommentToOutgoingDto(commentRepository.save(comment));
    }

    @Override
    public OutgoingItemDto patchItemById(Long ownerId, Long itemId, IncomingItemDto incomingItemDto) {
        Item item = itemRepository.getItemById(itemId);
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new NotFoundException("The updating item don't belong to user with id " + ownerId + "!");
        }
        if (null != incomingItemDto.getName() && !incomingItemDto.getName().isBlank()) {
            item.setName(incomingItemDto.getName());
        }
        if (null != incomingItemDto.getDescription() && !incomingItemDto.getDescription().isBlank()) {
            item.setDescription(incomingItemDto.getDescription());
        }
        if (null != incomingItemDto.getAvailable()) {
            item.setAvailable(incomingItemDto.getAvailable());
        }
        return ItemMapper.mapItemToOutgoingDto(itemRepository.save(item));
    }

    @Override
    public OutgoingItemDto deleteItemById(Long ownerId, Long itemId) {
        Item item = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("Owner with id " + ownerId
                        + " has no item with id " + itemId + "!"));
        itemRepository.deleteById(item.getId());
        return ItemMapper.mapItemToOutgoingDto(item);
    }

    @Override
    public void deleteAllOwnerItems(Long ownerId) {
        itemRepository.deleteByOwnerId(ownerId);
    }

    public LastNextBookingDto getNextBookingByItemId(Long itemId) {
        Booking booking = bookingRepository
                .findFirstByItemIdAndStartIsAfterAndStatusIs(itemId, LocalDateTime.now(),
                        BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start")).orElse(null);
        return booking == null ? null : BookingMapper.mapBookingToLastNextDto(booking);
    }

    public LastNextBookingDto getLastBookingByItemId(Long itemId) {
        Booking booking = bookingRepository
                .findFirstByItemIdAndStartIsBeforeAndStatusIs(itemId, LocalDateTime.now(),
                        BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end")).orElse(null);
        return booking == null ? null : BookingMapper.mapBookingToLastNextDto(booking);
    }

    private List<OutgoingCommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::mapCommentToOutgoingDto)
                .collect(toList());
    }
}