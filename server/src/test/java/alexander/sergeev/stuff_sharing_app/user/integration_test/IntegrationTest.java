package alexander.sergeev.stuff_sharing_app.user.integration_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private BookingService bookingService;
    private final Pageable pageable = PageRequest.of(0, 20);
    private final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusHours(1);
    private final User owner = new User(
            1L,
            "Owner name",
            "owner@email.com");
    private final UserDto ownerDto = new UserDto(
            null,
            "Owner name",
            "owner@email.com");
    private final User booker = new User(
            2L,
            "Booker name",
            "booker@email.com");
    private final UserDto bookerDto = new UserDto(
            null,
            "Booker name",
            "booker@email.com");
    private final Item item = new Item(
            1L,
            "Item Name",
            "Item description",
            true,
            null,
            owner);
    private final IncomingItemDto incomingItemDto = new IncomingItemDto(
            "Item Name",
            "Item description",
            true,
            1L);
    private final OutgoingItemDto outgoingItemDto = new OutgoingItemDto(
            1L,
            "Item Name",
            "Item description",
            true,
            null,
            null,
            List.of(),
            1L);
    private final Request request = new Request(
            1L,
            "Request description",
            null,
            booker);
    private final IncomingRequestDto incomingRequestDto = new IncomingRequestDto(
            null,
            "Request description");
    private final OutgoingRequestDto outgoingRequestDto = new OutgoingRequestDto(
            1L,
            "Request description",
            null,
            2L,
            List.of(outgoingItemDto));
    private final IncomingBookingDto incomingBookingDtoLast = new IncomingBookingDto(
            null,
            now.minusDays(3),
            now.minusDays(2),
            1L);
    private final OutgoingBookingDto outgoingBookingDtoLast = new OutgoingBookingDto(
            1L,
            now.minusDays(3),
            now.minusDays(2),
            booker,
            item,
            BookingStatus.APPROVED);
    private final IncomingBookingDto incomingBookingDtoCurrent = new IncomingBookingDto(
            null,
            now.minusDays(1),
            now.plusDays(1),
            1L);
    private final OutgoingBookingDto outgoingBookingDtoCurrent = new OutgoingBookingDto(
            2L,
            now.minusDays(1),
            now.plusDays(1),
            booker,
            item,
            BookingStatus.APPROVED);
    private final LastNextBookingDto lastNextBookingDtoLast = new LastNextBookingDto(
            2L,
            2L);
    private final IncomingBookingDto incomingBookingDtoNext = new IncomingBookingDto(
            null,
            now.plusDays(2),
            now.plusDays(3),
            1L);
    private final OutgoingBookingDto outgoingBookingDtoNext = new OutgoingBookingDto(
            3L,
            now.plusDays(2),
            now.plusDays(3),
            booker,
            item,
            BookingStatus.WAITING);
    private final LastNextBookingDto lastNextBookingDtoNext = new LastNextBookingDto(
            3L,
            2L);
    private final Comment comment = new Comment(
            1L,
            "Comment text",
            item,
            booker,
            null);
    private final IncomingCommentDto incomingCommentDto = new IncomingCommentDto(
            null,
            "Comment text");
    private final OutgoingCommentDto outgoingCommentDto = new OutgoingCommentDto(
            1L,
            "Comment text",
            "Booker name",
            null);

    @BeforeEach
    void setUp() {
        userService.postUser(ownerDto);
        userService.postUser(bookerDto);
        requestService.postRequest(2L, incomingRequestDto);
        itemService.postItem(1L, incomingItemDto);
        bookingService.postBooking(2L, incomingBookingDtoLast);
        bookingService.postBooking(2L, incomingBookingDtoCurrent);
        bookingService.postBooking(2L, incomingBookingDtoNext);
        bookingService.patchBookingById(1L, 1L, true);
        bookingService.patchBookingById(1L, 2L, true);
        bookingService.patchBookingById(1L, 3L, true);
        itemService.postComment(2L, 1L, incomingCommentDto);
        ownerDto.setId(1L);
        bookerDto.setId(2L);
        outgoingItemDto.setLastBooking(lastNextBookingDtoLast);
        outgoingItemDto.setNextBooking(lastNextBookingDtoNext);
        outgoingItemDto.setComments(List.of(outgoingCommentDto));
        comment.setCreated(itemService.getItemDtoById(1L, 1L).getComments().get(0).getCreated());
        outgoingCommentDto.setCreated(comment.getCreated());
        request.setCreated(requestService.getRequestById(2L, 1L).getCreated());
        outgoingRequestDto.setCreated(request.getCreated());
        outgoingBookingDtoLast.setStatus(BookingStatus.APPROVED);
        outgoingBookingDtoCurrent.setStatus(BookingStatus.APPROVED);
        outgoingBookingDtoNext.setStatus(BookingStatus.APPROVED);
        outgoingBookingDtoLast.getItem().setRequest(request);
        outgoingBookingDtoCurrent.getItem().setRequest(request);
        outgoingBookingDtoNext.getItem().setRequest(request);
    }

    /*
    User
     */

    @Test
    void getAllUsers() {
        List<UserDto> expected = List.of(ownerDto, bookerDto);
        List<UserDto> result = userService.getAllUsers(pageable);
        assertEquals(expected, result);
    }

    @Test
    void getUserById() {
        UserDto expected = ownerDto;
        UserDto result = userService.getUserById(1L);
        assertEquals(expected, result);
    }

    @Test
    void patchUserById() {
        ownerDto.setName("Updated name");
        ownerDto.setEmail("updated@email.com");
        UserDto expected = ownerDto;
        userService.patchUserById(1L, ownerDto);
        UserDto result = userService.getUserById(1L);
        assertEquals(expected, result);
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(1L);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUserById(1L));
        assertEquals("There's no user with id 1", notFoundException.getMessage());
    }

    /*
    Item
     */

    @Test
    void getAllOwnerItems() {
        List<OutgoingItemDto> expected = List.of(outgoingItemDto);
        List<OutgoingItemDto> result = itemService.getAllOwnerItems(1L, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getItemDtoById() {
        OutgoingItemDto expected = outgoingItemDto;
        OutgoingItemDto result = itemService.getItemDtoById(1L, 1L);
        assertEquals(expected, result);
    }

    @Test
    void getItemsBySearch() {
        outgoingItemDto.setLastBooking(null);
        outgoingItemDto.setNextBooking(null);
        List<OutgoingItemDto> expected = List.of(outgoingItemDto);
        List<OutgoingItemDto> result = itemService.getItemsBySearch(1L, "ItEm NaMe", pageable);
        assertEquals(expected, result);
    }

    @Test
    void patchItemById() {
        incomingItemDto.setName("Updated name");
        incomingItemDto.setDescription("Updated description");
        incomingItemDto.setAvailable(false);
        outgoingItemDto.setName("Updated name");
        outgoingItemDto.setDescription("Updated description");
        outgoingItemDto.setAvailable(false);
        outgoingItemDto.setLastBooking(null);
        outgoingItemDto.setNextBooking(null);
        outgoingItemDto.setComments(List.of());
        OutgoingItemDto expected = outgoingItemDto;
        OutgoingItemDto result = itemService.patchItemById(1L, 1L, incomingItemDto);
        assertEquals(expected, result);
    }

    @Test
    void deleteItemById() {
        itemService.deleteItemById(1L, 1L);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.getItemDtoById(1L, 1L));
        assertEquals("There's no item with id 1", notFoundException.getMessage());
    }

    @Test
    void deleteAllOwnerItems() {
        itemService.deleteAllOwnerItems(1L);
        List<OutgoingItemDto> expected = List.of();
        List<OutgoingItemDto> result = itemService.getAllOwnerItems(1L, pageable);
        assertEquals(expected, result);
    }

    /*
    Request
     */

    @Test
    void getAllRequesterRequests() {
        outgoingItemDto.setLastBooking(null);
        outgoingItemDto.setNextBooking(null);
        outgoingItemDto.setComments(List.of());
        List<OutgoingRequestDto> expected = List.of(outgoingRequestDto);
        List<OutgoingRequestDto> result = requestService.getAllRequesterRequests(2L, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllRequests() {
        outgoingItemDto.setLastBooking(null);
        outgoingItemDto.setNextBooking(null);
        outgoingItemDto.setComments(List.of());
        List<OutgoingRequestDto> expected = List.of(outgoingRequestDto);
        List<OutgoingRequestDto> result = requestService.getAllRequests(1L, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getRequestById() {
        outgoingItemDto.setLastBooking(null);
        outgoingItemDto.setNextBooking(null);
        outgoingItemDto.setComments(List.of());
        OutgoingRequestDto expected = outgoingRequestDto;
        OutgoingRequestDto result = requestService.getRequestById(1L, 1L);
        assertEquals(expected, result);
    }

    /*
    Booking
     */

    @Test
    void getAllUserBookings_all() {
        List<OutgoingBookingDto> expected = List.of(outgoingBookingDtoLast,
                outgoingBookingDtoCurrent, outgoingBookingDtoNext);
        List<OutgoingBookingDto> result = bookingService.getAllUserBookings(2L,
                BookingState.ALL, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllUserBookings_past() {
        List<OutgoingBookingDto> expected = List.of(outgoingBookingDtoLast);
        List<OutgoingBookingDto> result = bookingService.getAllUserBookings(2L,
                BookingState.PAST, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllUserBookings_current() {
        List<OutgoingBookingDto> expected = List.of(outgoingBookingDtoCurrent);
        List<OutgoingBookingDto> result = bookingService.getAllUserBookings(2L,
                BookingState.CURRENT, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllUserBookings_future() {
        List<OutgoingBookingDto> expected = List.of(outgoingBookingDtoNext);
        List<OutgoingBookingDto> result = bookingService.getAllUserBookings(2L,
                BookingState.FUTURE, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllUserBookings_waiting() {
        List<OutgoingBookingDto> expected = List.of(bookingService.postBooking(2L,
                new IncomingBookingDto(null, now.plusDays(3), now.plusDays(4), 1L)));
        List<OutgoingBookingDto> result = bookingService.getAllUserBookings(2L,
                BookingState.WAITING, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllUserBookings_rejected() {
        bookingService.postBooking(2L, new IncomingBookingDto(null,
                now.plusDays(3), now.plusDays(4), 1L));
        List<OutgoingBookingDto> expected = List.of(bookingService.patchBookingById(1L,
                4L, false));
        List<OutgoingBookingDto> result = bookingService.getAllUserBookings(2L,
                BookingState.REJECTED, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllOwnerItemBookings_all() {
        List<OutgoingBookingDto> expected = List.of(outgoingBookingDtoLast,
                outgoingBookingDtoCurrent, outgoingBookingDtoNext);
        List<OutgoingBookingDto> result = bookingService.getAllOwnerItemBookings(1L,
                BookingState.ALL, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllOwnerItemBookings_past() {
        List<OutgoingBookingDto> expected = List.of(outgoingBookingDtoLast);
        List<OutgoingBookingDto> result = bookingService.getAllOwnerItemBookings(1L,
                BookingState.PAST, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllOwnerItemBookings_current() {
        List<OutgoingBookingDto> expected = List.of(outgoingBookingDtoCurrent);
        List<OutgoingBookingDto> result = bookingService.getAllOwnerItemBookings(1L,
                BookingState.CURRENT, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllOwnerItemBookings_future() {
        List<OutgoingBookingDto> expected = List.of(outgoingBookingDtoNext);
        List<OutgoingBookingDto> result = bookingService.getAllOwnerItemBookings(1L,
                BookingState.FUTURE, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllOwnerItemBookings_waiting() {
        List<OutgoingBookingDto> expected = List.of(bookingService.postBooking(2L,
                new IncomingBookingDto(null, now.plusDays(3), now.plusDays(3), 1L)));
        List<OutgoingBookingDto> result = bookingService.getAllOwnerItemBookings(1L,
                BookingState.WAITING, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getAllOwnerItemBookings_rejected() {
        bookingService.postBooking(2L, new IncomingBookingDto(null,
                now.plusDays(3), now.plusDays(3), 1L));
        List<OutgoingBookingDto> expected = List.of(bookingService.patchBookingById(1L,
                4L, false));
        List<OutgoingBookingDto> result = bookingService.getAllOwnerItemBookings(1L,
                BookingState.REJECTED, pageable);
        assertEquals(expected, result);
    }

    @Test
    void getBookingById() {
        OutgoingBookingDto expected = outgoingBookingDtoLast;
        OutgoingBookingDto result = bookingService.getBookingById(2L, 1L);
        assertEquals(expected, result);
    }

}
