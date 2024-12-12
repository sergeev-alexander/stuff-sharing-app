package alexander.sergeev.stuff_sharing_app.booking.service;

import alexander.sergeev.stuff_sharing_app.booking.dto.IncomingBookingDto;
import alexander.sergeev.stuff_sharing_app.booking.dto.OutgoingBookingDto;
import alexander.sergeev.stuff_sharing_app.booking.model.BookingState;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    List<OutgoingBookingDto> getAllUserBookings(Long userId, BookingState bookingState, Pageable pageable);

    List<OutgoingBookingDto> getAllOwnerItemBookings(Long ownerId, BookingState bookingState, Pageable pageable);

    OutgoingBookingDto getBookingById(Long userId, Long bookingId);

    OutgoingBookingDto postBooking(Long bookerId, IncomingBookingDto incomingBookingDto);

    OutgoingBookingDto patchBookingById(Long itemOwnerId, Long bookingId, Boolean approved);

}