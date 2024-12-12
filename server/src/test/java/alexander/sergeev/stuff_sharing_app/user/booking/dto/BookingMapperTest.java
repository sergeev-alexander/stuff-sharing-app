package alexander.sergeev.stuff_sharing_app.user.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class BookingMapperTest {

    @Test
    void mapBookingToOutgoingDto() {
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                new Item(
                        1L,
                        "Item name",
                        "Item description",
                        true,
                        null,
                        new User(
                                1L,
                                "Owner name",
                                "owner@email.com")),
                new User(
                        2L,
                        "Booker name",
                        "booker@email.com"),
                BookingStatus.WAITING);
        OutgoingBookingDto result = BookingMapper.mapBookingToOutgoingDto(booking);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getItem(), result.getItem());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void mapIncomingDtoToBooking() {
        IncomingBookingDto incomingBookingDto = new IncomingBookingDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                1L);
        Booking result = BookingMapper.mapIncomingDtoToBooking(incomingBookingDto);
        assertEquals(incomingBookingDto.getId(), result.getId());
        assertEquals(incomingBookingDto.getStart(), result.getStart());
        assertEquals(incomingBookingDto.getEnd(), result.getEnd());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void mapBookingToLastNextDto() {
        Booking booking = new Booking(
                1L,
                null,
                null,
                null,
                new User(
                        1L,
                        null,
                        null),
                null);
        LastNextBookingDto result = BookingMapper.mapBookingToLastNextDto(booking);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getBooker().getId(), result.getBookerId());
    }

}
