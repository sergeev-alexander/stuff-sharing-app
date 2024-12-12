package alexander.sergeev.stuff_sharing_app.user.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingServiceIml;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private OutgoingBookingDto outgoingBookingDto;
    private final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    private final Pageable pageable = PageRequest.of(0, 20);

    @BeforeEach
    void setUp() {
        owner = new User(
                1L,
                "Owner name",
                "owner@email.com");
        booker = new User(
                2L,
                "Booker name",
                "booker@email.com");
        item = new Item(
                1L,
                "Item name",
                "Item description",
                true,
                null,
                owner);
        outgoingBookingDto = new OutgoingBookingDto(
                1L,
                now.minusDays(1),
                now.plusDays(1),
                booker,
                item,
                BookingStatus.APPROVED);
        booking = new Booking(
                1L,
                now.minusDays(1),
                now.plusDays(1),
                item,
                booker,
                BookingStatus.WAITING);
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getAllUserBookings_whenInvoked_shouldInvokeRepositoryMethods(BookingState bookingState) {
        bookingServiceIml.getAllUserBookings(anyLong(), bookingState, pageable);
        verify(userRepository).checkUserById(anyLong());
        switch (bookingState) {
            case ALL:
                verify(bookingRepository).findByBookerId(anyLong(),
                        any(Pageable.class));
                break;
            case CURRENT:
                verify(bookingRepository).findByBookerIdAndEndIsAfterAndStartIsBefore(anyLong(),
                        any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
                break;
            case PAST:
                verify(bookingRepository).findByBookerIdAndEndIsBefore(anyLong(),
                        any(LocalDateTime.class), any(Pageable.class));
                break;
            case FUTURE:
                verify(bookingRepository).findByBookerIdAndStartIsAfter(anyLong(),
                        any(LocalDateTime.class), any(Pageable.class));
                break;
            case WAITING:
            case REJECTED:
                verify(bookingRepository).findByBookerIdAndStatusIs(anyLong(),
                        any(BookingStatus.class), any(Pageable.class));
                break;
        }
    }

    @ParameterizedTest
    @EnumSource(BookingState.class)
    void getAllOwnerItemBookings_whenInvoked_shouldInvokeRepositoryMethods(BookingState bookingState) {
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(List.of(item));
        bookingServiceIml.getAllOwnerItemBookings(anyLong(), bookingState, pageable);
        verify(itemRepository).findByOwnerId(anyLong());
        switch (bookingState) {
            case ALL:
                verify(bookingRepository).findByItemIdIn(anyList(), any(Pageable.class));
                break;
            case CURRENT:
                verify(bookingRepository).findByItemIdInAndStartIsBeforeAndEndIsAfter(anyList(),
                        any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
                break;
            case PAST:
                verify(bookingRepository).findByItemIdInAndEndIsBefore(anyList(),
                        any(LocalDateTime.class), any(Pageable.class));
                break;
            case FUTURE:
                verify(bookingRepository).findByItemIdInAndStartIsAfter(anyList(),
                        any(LocalDateTime.class), any(Pageable.class));
                break;
            case WAITING:
            case REJECTED:
                verify(bookingRepository).findByItemIdInAndStatusIs(anyList(),
                        any(BookingStatus.class), any(Pageable.class));
                break;
        }
    }

    @Test
    void getAllOwnerItemBookings_whenUserHasNoItems_shouldThrowNotFoundException() {
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(List.of());
        for (BookingState bookingState : BookingState.values()) {
            NotFoundException notFoundException = assertThrows(NotFoundException.class,
                    () -> bookingServiceIml.getAllOwnerItemBookings(anyLong(), bookingState,
                            pageable));
            assertTrue(notFoundException.getMessage().contains("There's no items belong to user"));
        }
        verify(itemRepository, times(6)).findByOwnerId(anyLong());
    }

    @Test
    void getBookingById_whenBookingExists_shouldInvokeRepositoryMethods_andReturnBooking() {
        outgoingBookingDto.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        OutgoingBookingDto result = bookingServiceIml.getBookingById(1L, 1L);
        assertEquals(outgoingBookingDto, result);
        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBookingById_whenBookingNotApplicableToUser_shouldThrowNotFoundException() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceIml.getBookingById(3L, 1L));
        assertEquals("Requesting booking is not created by user 3 and booking item don't belong to him!",
                notFoundException.getMessage());
        verify(bookingRepository).findById(1L);
    }

    @Test
    void postBooking_whenInvoke_shouldInvokeRepositoryMethods() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                now.minusDays(1),
                now.plusDays(1),
                1L);
        Booking booking = new Booking(
                1L,
                now.minusDays(1),
                now.plusDays(1),
                item,
                booker,
                BookingStatus.WAITING);
        when(userRepository.getUserById(2L))
                .thenReturn(booker);
        when(itemRepository.getItemById(1L))
                .thenReturn(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        OutgoingBookingDto expected = BookingMapper.mapBookingToOutgoingDto(booking);
        OutgoingBookingDto result = bookingServiceIml.postBooking(2L, incomingBookingDto);
        assertEquals(expected, result);
        verify(userRepository).getUserById(2L);
        verify(itemRepository).getItemById(1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void postBooking_whenBookingItemBelongToUser_shouldThrowNotFoundException() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                now.minusDays(1),
                now.plusDays(1),
                1L);
        when(userRepository.getUserById(1L))
                .thenReturn(owner);
        when(itemRepository.getItemById(1L))
                .thenReturn(item);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceIml.postBooking(1L, incomingBookingDto));
        assertEquals("Booking item belongs to booker!", notFoundException.getMessage());
    }

    @Test
    void postBooking_whenBookingItemStatusIsNotAvailable_shouldThrowNotAvailableItemException() {
        item.setAvailable(false);
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                null,
                now.minusDays(1),
                now.plusDays(1),
                1L);
        when(userRepository.getUserById(2L))
                .thenReturn(booker);
        when(itemRepository.getItemById(1L))
                .thenReturn(item);
        NotAvailableItemException notAvailableItemException = assertThrows(NotAvailableItemException.class,
                () -> bookingServiceIml.postBooking(2L, incomingBookingDto));
        assertEquals("Booking item is not available!", notAvailableItemException.getMessage());
    }

    @Test
    void patchBookingById_whenBookingStatusIsWaiting_shouldChangeItAccordingApprovedCondition_andInvokeRepositoryMethods() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);
        OutgoingBookingDto result = bookingServiceIml.patchBookingById(1L, 1L, true);
        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void patchBookingById_whenBookingStatusIsNotWaiting_shouldThrowNotAvailableItemException() {
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        NotAvailableItemException notAvailableItemException = assertThrows(NotAvailableItemException.class,
                () -> bookingServiceIml.patchBookingById(1L, 1L, true));
        assertEquals("Not allowed to change booking status REJECTED",
                notAvailableItemException.getMessage());
    }

    @Test
    void patchBookingById_whenBookingItemDoNotBelongToUser_shouldThrowNotFoundException() {
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingServiceIml.patchBookingById(2L, 1L, true));
        assertEquals("Booking item don't belong to user with id 2",
                notFoundException.getMessage());
    }

}
