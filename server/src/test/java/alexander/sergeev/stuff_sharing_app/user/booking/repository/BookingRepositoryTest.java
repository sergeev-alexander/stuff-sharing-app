package alexander.sergeev.stuff_sharing_app.user.booking.repository;

import alexander.sergeev.stuff_sharing_app.booking.dto.BookingMapper;
import alexander.sergeev.stuff_sharing_app.booking.dto.LastNextBookingDto;
import alexander.sergeev.stuff_sharing_app.booking.model.Booking;
import alexander.sergeev.stuff_sharing_app.booking.model.BookingStatus;
import alexander.sergeev.stuff_sharing_app.booking.repository.BookingRepository;
import alexander.sergeev.stuff_sharing_app.item.model.Item;
import alexander.sergeev.stuff_sharing_app.item.repository.ItemRepository;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import alexander.sergeev.stuff_sharing_app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    private final Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "start"));

    private Booking pastBooking;

    private Booking currentBooking;

    private Booking futureBooking;

    private final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    @BeforeEach
    void setUp() {
        User owner = new User(
                null,
                "Owner name",
                "owner@email.com");
        User booker = new User(
                null,
                "Booker name",
                "booker@email.com");
        Item item = new Item(
                null,
                "Item name",
                "Item description",
                true,
                null,
                owner);
        pastBooking = new Booking(
                null,
                now.minusDays(3L),
                now.minusDays(2L),
                item,
                booker,
                BookingStatus.APPROVED);
        currentBooking = new Booking(
                null,
                now.minusDays(1L),
                now.plusDays(1L),
                item,
                booker,
                BookingStatus.APPROVED);
        futureBooking = new Booking(
                null,
                now.plusDays(2L),
                now.plusDays(3L),
                item,
                booker,
                BookingStatus.APPROVED);
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
    }

    @Test
    void findByBookerId_whenBookingExists_shouldReturnBookingList() {
        bookingRepository.save(currentBooking);
        List<Booking> expected = List.of(currentBooking);
        List<Booking> result = bookingRepository.findByBookerId(2L, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByBookerId_whenBookingDoesNotExist_shouldReturnEmptyList() {
        List<Booking> result = bookingRepository.findByBookerId(2L, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_whenBookingPresent_shouldReturnOptionalOfBooking() {
        bookingRepository.save(currentBooking);
        Optional<Booking> result = bookingRepository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(currentBooking, result.get());
    }

    @Test
    void findById_whenBookingNotPresent_shouldReturnEmptyOptional() {
        Optional<Booking> result = bookingRepository.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void findByBookerIdAndEndIsAfterAndStartIsBefore_whenBookingExists_shouldReturnBookingList() {
        saveThreeBookings();
        List<Booking> expected = List.of(currentBooking);
        List<Booking> result = bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(
                2L, now, now, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByBookerIdAndEndIsAfterAndStartIsBefore_whenBookingDoesNotExist_shouldReturnEmptyList() {
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
        List<Booking> result = bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(
                2L, now, now, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByBookerIdAndEndIsBefore_whenBookingExists_shouldReturnBookingList() {
        saveThreeBookings();
        List<Booking> expected = List.of(pastBooking);
        List<Booking> result = bookingRepository.findByBookerIdAndEndIsBefore(
                2L, now, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByBookerIdAndEndIsBefore_whenBookingDoesNotExist_shouldReturnEmptyList() {
        bookingRepository.save(currentBooking);
        bookingRepository.save(futureBooking);
        List<Booking> result = bookingRepository.findByBookerIdAndEndIsBefore(
                2L, now, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByBookerIdAndStartIsAfter_whenBookingExists_shouldReturnBookingList() {
        saveThreeBookings();
        List<Booking> expected = List.of(futureBooking);
        List<Booking> result = bookingRepository.findByBookerIdAndStartIsAfter(
                2L, now, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByBookerIdAndStartIsAfter_whenBookingDoesNotExist_shouldReturnAnEmptyList() {
        bookingRepository.save(currentBooking);
        bookingRepository.save(pastBooking);
        List<Booking> result = bookingRepository.findByBookerIdAndStartIsAfter(
                2L, now, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByBookerIdAndStatusIs_whenBookingExists_shouldReturnBookingList() {
        bookingRepository.save(currentBooking);
        List<Booking> expected = List.of(currentBooking);
        List<Booking> result = bookingRepository.findByBookerIdAndStatusIs(2L,
                BookingStatus.APPROVED, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByBookerIdAndStatusIs_whenBookingDoesNotExist_shouldReturnAnEmptyList() {
        bookingRepository.save(currentBooking);
        List<Booking> result = bookingRepository.findByBookerIdAndStatusIs(2L,
                BookingStatus.REJECTED, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemIdIn_whenBookingExists_shouldReturnBookingList() {
        bookingRepository.save(currentBooking);
        List<Booking> expected = List.of(currentBooking);
        List<Booking> result = bookingRepository.findByItemIdIn(List.of(1L), pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByItemIdIn_whenBookingDoesNotExist_shouldReturnAnEmptyList() {
        saveThreeBookings();
        List<Booking> result = bookingRepository.findByItemIdIn(List.of(123L), pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemIdInAndStartIsBeforeAndEndIsAfter_whenBookingExists_shouldReturnIt() {
        saveThreeBookings();
        List<Booking> expected = List.of(currentBooking);
        List<Booking> result = bookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(
                List.of(1L), now, now, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByItemIdInAndStartIsBeforeAndEndIsAfter_whenBookingDoesNotExist_shouldReturnAnEmptyList() {
        saveThreeBookings();
        List<Booking> result = bookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(
                List.of(123L), now, now, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemIdInAndEndIsBefore_whenBookingExists_shouldReturnIt() {
        saveThreeBookings();
        List<Booking> expected = List.of(pastBooking);
        List<Booking> result = bookingRepository.findByItemIdInAndEndIsBefore(
                List.of(1L), now, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByItemIdInAndEndIsBefore_whenBookingDoesNotExist_shouldReturnAnEmptyList() {
        saveThreeBookings();
        List<Booking> result = bookingRepository.findByItemIdInAndEndIsBefore(
                List.of(123L), now, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemIdInAndStartIsAfter_whenBookingExists_shouldReturnIt() {
        saveThreeBookings();
        List<Booking> expected = List.of(futureBooking);
        List<Booking> result = bookingRepository.findByItemIdInAndStartIsAfter(
                List.of(1L), now, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByItemIdInAndStartIsAfter_whenBookingDoesNotExist_shouldReturnAnEmptyList() {
        saveThreeBookings();
        List<Booking> result = bookingRepository.findByItemIdInAndStartIsAfter(
                List.of(123L), now, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemIdInAndStatusIs_whenBookingExists_shouldReturnIt() {
        bookingRepository.save(currentBooking);
        List<Booking> expected = List.of(currentBooking);
        List<Booking> result = bookingRepository.findByItemIdInAndStatusIs(
                List.of(1L), BookingStatus.APPROVED, pageable);
        assertEquals(expected, result);
    }

    @Test
    void findByItemIdInAndStatusIs_whenBookingDoesNotExist_shouldReturnAnEmptyList() {
        saveThreeBookings();
        List<Booking> result = bookingRepository.findByItemIdInAndStatusIs(
                List.of(1L), BookingStatus.WAITING, pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findFirstByItemIdAndStartIsAfterAndStatusIs_whenBookingExists_shouldReturnOptionalOfBooking() {
        saveThreeBookings();
        LastNextBookingDto expected = new LastNextBookingDto(
                futureBooking.getId(),
                futureBooking.getBooker().getId());
        Optional<LastNextBookingDto> result = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIs(
                1L, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                .map(BookingMapper::mapBookingToLastNextDto);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    void findFirstByItemIdAndStartIsAfterAndStatusIs_whenBookingDoesNotExist_shouldReturnEmptyOptional() {
        saveThreeBookings();
        Optional<LastNextBookingDto> result = bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIs(
                123L, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                .map(BookingMapper::mapBookingToLastNextDto);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void findFirstByItemIdAndStartIsBeforeAndStatusIs_whenBookingExists_shouldReturnOptionalOfBooking() {
        saveThreeBookings();
        LastNextBookingDto expected = new LastNextBookingDto(
                currentBooking.getId(),
                currentBooking.getBooker().getId());
        Optional<LastNextBookingDto> result = bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusIs(
                1L, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"))
                .map(BookingMapper::mapBookingToLastNextDto);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    void findFirstByItemIdAndStartIsBeforeAndStatusIs_whenBookingDoesNotExist_shouldReturnEmptyOptional() {
        saveThreeBookings();
        Optional<LastNextBookingDto> result = bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusIs(
                123L, now, BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"))
                .map(BookingMapper::mapBookingToLastNextDto);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    void existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs_whenBookingExists_shouldReturnTrue() {
        saveThreeBookings();
        Boolean result = bookingRepository.existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(
                2L, 1L, now, BookingStatus.APPROVED);
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    void existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs_whenBookingDoesNotExist_shouldReturnFalse() {
        saveThreeBookings();
        Boolean result = bookingRepository.existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(
                1L, 1L, now, BookingStatus.APPROVED);
        assertNotNull(result);
        assertFalse(result);
    }

    private void saveThreeBookings() {
        bookingRepository.save(currentBooking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
    }
}