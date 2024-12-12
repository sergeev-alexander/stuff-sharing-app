package alexander.sergeev.stuff_sharing_app.user.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemServiceImp;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RequestRepository requestRepository;
    private final Pageable pageable = PageRequest.of(0, 20);
    private User owner;
    private User author;
    private Request request;
    private Item item;
    private Comment comment;
    private Booking lastBooking;
    private Booking nextBooking;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        owner = new User(
                1L,
                "Owner name",
                "owner@email.com");
        User requester = new User(
                2L,
                "Requester name",
                "requester@email.com");
        author = new User(
                3L,
                "Author name",
                "author@email.com");
        User booker = new User(
                4L,
                "Booker name",
                "booker@email.com");
        request = new Request(
                1L,
                "Request description",
                now,
                requester);
        item = new Item(
                1L,
                "Item name",
                "Item description",
                true,
                request,
                owner);
        comment = new Comment(
                1L,
                "Comment text",
                item,
                author,
                now);
        lastBooking = new Booking(
                1L,
                now.minusDays(2L),
                now.minusDays(1L),
                item,
                booker,
                BookingStatus.APPROVED);
        nextBooking = new Booking(
                2L,
                now.plusDays(1L),
                now.plusDays(2L),
                item,
                booker,
                BookingStatus.APPROVED);
    }

    @Test
    void getAllOwnerItems_whenInvoke_shouldInvokeRepositoryMethods_andReturnItemList() {
        when(itemRepository.findByOwnerId(1L, pageable))
                .thenReturn(List.of(item));
        when(commentRepository.findByItemIdIn(List.of(1L)))
                .thenReturn(List.of(comment));
        when(bookingRepository.findByItemIdIn(List.of(1L), pageable))
                .thenReturn(List.of(lastBooking, nextBooking));
        OutgoingItemDto outgoingItemDto = ItemMapper.mapItemToOutgoingDto(item);
        outgoingItemDto.setLastBooking(BookingMapper.mapBookingToLastNextDto(lastBooking));
        outgoingItemDto.setNextBooking(BookingMapper.mapBookingToLastNextDto(nextBooking));
        OutgoingCommentDto outgoingCommentDto = CommentMapper.mapCommentToOutgoingDto(comment);
        outgoingItemDto.setComments(List.of(outgoingCommentDto));
        List<OutgoingItemDto> expected = List.of(outgoingItemDto);
        List<OutgoingItemDto> result = itemServiceImp.getAllOwnerItems(1L, pageable);
        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository).checkUserById(1L);
        verify(itemRepository).findByOwnerId(1L, pageable);
        verify(bookingRepository).findByItemIdIn(List.of(1L), pageable);
    }

    @Test
    void getItemDtoById_whenOwnerInvoke_shouldInvokeRepositoryMethods_andReturnItem() {
        when(itemRepository.getItemById(1L))
                .thenReturn(item);
        when(bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusIs(anyLong(), any(LocalDateTime.class),
                any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIs(anyLong(), any(LocalDateTime.class),
                any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Optional.of(nextBooking));
        when(commentRepository.findByItemId(1L))
                .thenReturn(List.of(comment));
        OutgoingItemDto expected = ItemMapper.mapItemToOutgoingDto(item);
        expected.setLastBooking(BookingMapper.mapBookingToLastNextDto(lastBooking));
        expected.setNextBooking(BookingMapper.mapBookingToLastNextDto(nextBooking));
        OutgoingCommentDto outgoingCommentDto = CommentMapper.mapCommentToOutgoingDto(comment);
        expected.setComments(List.of(outgoingCommentDto));
        OutgoingItemDto result = itemServiceImp.getItemDtoById(1L, 1L);
        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository).checkUserById(1L);
        verify(itemRepository).getItemById(1L);
        verify(bookingRepository).findFirstByItemIdAndStartIsAfterAndStatusIs(anyLong(), any(LocalDateTime.class),
                any(BookingStatus.class), any(Sort.class));
        verify(bookingRepository).findFirstByItemIdAndStartIsBeforeAndStatusIs(anyLong(), any(LocalDateTime.class),
                any(BookingStatus.class), any(Sort.class));
    }

    @Test
    void getItemDtoById_whenNotOwnerInvoke_shouldInvokeRepositoryMethods_andReturnItem() {
        when(itemRepository.getItemById(1L))
                .thenReturn(item);
        when(commentRepository.findByItemId(1L))
                .thenReturn(List.of(comment));
        OutgoingItemDto expected = ItemMapper.mapItemToOutgoingDto(item);
        OutgoingCommentDto outgoingCommentDto = CommentMapper.mapCommentToOutgoingDto(comment);
        expected.setComments(List.of(outgoingCommentDto));
        OutgoingItemDto result = itemServiceImp.getItemDtoById(2L, 1L);
        assertNotNull(result);
        assertEquals(expected, result);
        verify(userRepository).checkUserById(2L);
        verify(itemRepository).getItemById(1L);
    }

    @Test
    void getItemsBySearch_whenInvoke_shouldInvokeRepositoryMethods_andReturnItemList() {
        when(itemRepository.searchByTextInNameOrDescriptionAndAvailableTrue(
                "item name", "item name", pageable))
                .thenReturn(List.of(item));
        when(commentRepository.findByItemIdIn(List.of(1L)))
                .thenReturn(List.of(comment));
        OutgoingCommentDto outgoingCommentDto = CommentMapper.mapCommentToOutgoingDto(comment);
        OutgoingItemDto outgoingItemDto = ItemMapper.mapItemToOutgoingDto(item);
        outgoingItemDto.setComments(List.of(outgoingCommentDto));
        List<OutgoingItemDto> expected = List.of(outgoingItemDto);
        List<OutgoingItemDto> result = itemServiceImp.getItemsBySearch(1L, "Item name", pageable);
        assertEquals(expected, result);
        verify(userRepository).checkUserById(1L);
        verify(itemRepository).searchByTextInNameOrDescriptionAndAvailableTrue(
                "item name", "item name", pageable);
        verify(commentRepository).findByItemIdIn(List.of(1L));
    }

    @Test
    void postItem_whenRequestIdIsPresent_shouldInvokeRepositoryMethods_andReturnItemWithRequest() {
        IncomingItemDto incomingItemDto = new IncomingItemDto(
                "Item name",
                "Item description",
                true,
                1L);
        when(userRepository.getUserById(1L))
                .thenReturn(owner);
        when(requestRepository.findRequestById(1L))
                .thenReturn(request);
        Item toSave = item;
        toSave.setId(null);
        when(itemRepository.save(toSave))
                .thenReturn(item);
        OutgoingItemDto expected = ItemMapper.mapItemToOutgoingDto(item);
        OutgoingItemDto result = itemServiceImp.postItem(1L, incomingItemDto);
        assertEquals(expected, result);
        verify(userRepository).getUserById(1L);
        verify(requestRepository).findRequestById(1L);
        verify(itemRepository).save(item);
    }

    @Test
    void postItem_whenRequestIdIsNotPresent_shouldInvokeRepositoryMethods_andReturnItemWithoutRequest() {
        IncomingItemDto incomingItemDto = new IncomingItemDto(
                "Item name",
                "Item description",
                true,
                null);
        when(userRepository.getUserById(1L))
                .thenReturn(owner);
        item.setRequest(null);
        Item toSave = item;
        toSave.setId(null);
        when(itemRepository.save(toSave))
                .thenReturn(item);
        OutgoingItemDto expected = ItemMapper.mapItemToOutgoingDto(item);
        OutgoingItemDto result = itemServiceImp.postItem(1L, incomingItemDto);
        assertEquals(expected, result);
        verify(userRepository).getUserById(1L);
        verify(itemRepository).save(item);
    }

    @Test
    void postComment_whenInvoke_shouldInvokeRepositoryMethods_andReturnComment() {
        IncomingCommentDto incomingCommentDto = new IncomingCommentDto(
                null,
                "Comment text");
        when(bookingRepository.existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(anyLong(),
                anyLong(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(true);
        when(userRepository.getUserById(3L))
                .thenReturn(author);
        when(itemRepository.getItemById(1L))
                .thenReturn(item);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        OutgoingCommentDto expected = CommentMapper.mapCommentToOutgoingDto(comment);
        OutgoingCommentDto result = itemServiceImp.postComment(3L, 1L, incomingCommentDto);
        assertEquals(expected, result);
        verify(bookingRepository).existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(anyLong(),
                anyLong(), any(LocalDateTime.class), any(BookingStatus.class));
        verify(userRepository).getUserById(3L);
        verify(itemRepository).getItemById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void postComment_whenAuthorDidNotUsedItem_shouldThrowNotAvailableItemException() {
        when(bookingRepository.existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(anyLong(),
                anyLong(), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(false);
        NotAvailableItemException notAvailableItemException = assertThrows(NotAvailableItemException.class,
                () -> itemServiceImp.postComment(3L, 1L, new IncomingCommentDto(null, "text")));
        assertEquals("Author with id 3 has no rights to leve a comment to item with id 1!",
                notAvailableItemException.getMessage());
    }

    @Test
    void patchItemById_whenInvoke_shouldInvokeRepositoryMethods_andReturnUpdatedItem() {
        IncomingItemDto incomingItemDto = new IncomingItemDto(
                "Updated name",
                "Updated description",
                false,
                null);
        Item toSave = new Item(
                1L,
                "Updated name",
                "Updated description",
                false,
                request,
                owner);
        when(itemRepository.getItemById(1L))
                .thenReturn(item);
        when(itemRepository.save(toSave))
                .thenReturn(toSave);
        OutgoingItemDto expected = ItemMapper.mapItemToOutgoingDto(toSave);
        OutgoingItemDto result = itemServiceImp.patchItemById(1L, 1L, incomingItemDto);
        assertEquals(expected, result);
        verify(itemRepository).getItemById(1L);
        verify(itemRepository).save(toSave);
    }

    @Test
    void patchItemById_whenItemDoNotBelongToUser_shouldThrowNotFoundException() {
        when(itemRepository.getItemById(1L))
                .thenReturn(item);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImp.patchItemById(4L, 1L, new IncomingItemDto(
                        "name", "description", true, null)));
        assertEquals("The updating item don't belong to user with id 4!",
                notFoundException.getMessage());
    }

    @Test
    void deleteItemById_whenInvoke_shouldInvokeRepositoryMethods() {
        when(itemRepository.findByIdAndOwnerId(1L, 1L))
                .thenReturn(Optional.of(item));
        itemServiceImp.deleteItemById(1L, 1L);
        verify(itemRepository).findByIdAndOwnerId(1L, 1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void deleteItemById_whenItemDoNotBelongToUser_shouldThrowNotFoundException() {
        when(itemRepository.findByIdAndOwnerId(1L, 4L))
                .thenReturn(Optional.empty());
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImp.deleteItemById(4L, 1L));
        assertEquals("Owner with id 4 has no item with id 1!",
                notFoundException.getMessage());
        verify(itemRepository).findByIdAndOwnerId(1L, 4L);
    }

    @Test
    void deleteAllOwnerItems_whenInvoke_shouldInvokeRepositoryMethod() {
        itemServiceImp.deleteAllOwnerItems(1L);
        verify(itemRepository).deleteByOwnerId(1L);
    }

    @Test
    void getNextBookingByItemId_whenInvoke_shouldInvokeRepositoryMethod() {
        itemServiceImp.getNextBookingByItemId(1L);
        verify(bookingRepository).findFirstByItemIdAndStartIsAfterAndStatusIs(anyLong(), any(LocalDateTime.class),
                any(BookingStatus.class), any(Sort.class));
    }

    @Test
    void getLastBookingByItemId_whenInvoke_shouldInvokeRepositoryMethod() {
        itemServiceImp.getLastBookingByItemId(1L);
        verify(bookingRepository).findFirstByItemIdAndStartIsBeforeAndStatusIs(anyLong(), any(LocalDateTime.class),
                any(BookingStatus.class), any(Sort.class));
    }
}