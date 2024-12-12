package alexander.sergeev.stuff_sharing_app.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static ru.practicum.shareit.http.HttpHeader.header;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
    private final BookingService bookingService;

    @GetMapping
    public Collection<OutgoingBookingDto> getAllUserBookings(
            HttpServletRequest request,
            @RequestParam(value = "from") Integer firstElement,
            @RequestParam(value = "size") Integer size,
            @RequestHeader(header) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String bookingStateString) {
        log.info("Id-{} {} {}?{}", userId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getAllUserBookings(userId, BookingState.valueOf(bookingStateString),
                PageRequest.of(firstElement / size, size, sortByStartDesc));
    }

    @GetMapping("/owner")
    public Collection<OutgoingBookingDto> getAllOwnerItemBookings(
            HttpServletRequest request,
            @RequestParam(value = "from") Integer firstElement,
            @RequestParam(value = "size") Integer size,
            @RequestHeader(header) Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String bookingStateString) {
        log.info("Id-{} {} {}?{}", ownerId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getAllOwnerItemBookings(ownerId, BookingState.valueOf(bookingStateString),
                PageRequest.of(firstElement / size, size, sortByStartDesc));
    }

    @GetMapping("/{bookingId}")
    public OutgoingBookingDto getBookingById(HttpServletRequest request,
                                             @RequestHeader(header) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Id-{} {} {}", userId, request.getMethod(), request.getRequestURI());
        return bookingService.getBookingById(userId, bookingId);
    }

    @PostMapping
    public OutgoingBookingDto postBooking(
            HttpServletRequest request,
            @RequestHeader(header) Long bookerId,
            @RequestBody IncomingBookingDto incomingBookingDto) {
        log.info("Id-{} {} {} {}", bookerId, request.getMethod(), request.getRequestURI(), incomingBookingDto);
        return bookingService.postBooking(bookerId, incomingBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public OutgoingBookingDto patchBooking(HttpServletRequest request,
                                           @RequestHeader(header) Long itemOwnerId,
                                           @PathVariable Long bookingId,
                                           @RequestParam Boolean approved) {
        log.info("Id-{} {} {}", itemOwnerId, request.getMethod(), request.getRequestURI());
        return bookingService.patchBookingById(itemOwnerId, bookingId, approved);
    }

}
