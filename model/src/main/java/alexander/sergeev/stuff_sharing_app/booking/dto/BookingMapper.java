package alexander.sergeev.stuff_sharing_app.booking.dto;

import alexander.sergeev.stuff_sharing_app.booking.model.Booking;
import alexander.sergeev.stuff_sharing_app.booking.model.BookingStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BookingMapper {

    public OutgoingBookingDto mapBookingToOutgoingDto(Booking booking) {
        return new OutgoingBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker(),
                booking.getItem(),
                booking.getStatus());
    }

    public Booking mapIncomingDtoToBooking(IncomingBookingDto incomingBookingDto) {
        return new Booking(
                incomingBookingDto.getId(),
                incomingBookingDto.getStart(),
                incomingBookingDto.getEnd(),
                null,
                null,
                BookingStatus.WAITING);
    }

    public LastNextBookingDto mapBookingToLastNextDto(Booking booking) {
        return new LastNextBookingDto(
                booking.getId(),
                booking.getBooker().getId());
    }
}