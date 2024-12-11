package alexander.sergeev.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(
            HttpServletRequest request,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size,
            @RequestHeader(header) @Positive Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") @BookingStateValidation String bookingStateString) {
        log.info("Id-{} {} {}?{}", userId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingClient.getAllUserBookings(userId, bookingStateString, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerItemBookings(
            HttpServletRequest request,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(20) Integer size,
            @RequestHeader(header) @Positive Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") @BookingStateValidation String bookingStateString) {
        log.info("Id-{} {} {}?{}", ownerId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingClient.getAllOwnerItemBookings(ownerId, bookingStateString, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(HttpServletRequest request,
                                                 @RequestHeader(header) @Positive Long userId,
                                                 @PathVariable @Positive Long bookingId) {
        log.info("Id-{} {} {}", userId, request.getMethod(), request.getRequestURI());
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> postBooking(
            HttpServletRequest request,
            @RequestHeader(header) @Positive Long bookerId,
            @RequestBody @Validated(ValidationMarker.OnCreate.class) IncomingBookingDto incomingBookingDto) {
        log.info("Id-{} {} {} {}", bookerId, request.getMethod(), request.getRequestURI(), incomingBookingDto);
        return bookingClient.postBooking(bookerId, incomingBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(HttpServletRequest request,
                                               @RequestHeader(header) @Positive Long itemOwnerId,
                                               @PathVariable @Positive Long bookingId,
                                               @RequestParam Boolean approved) {
        log.info("Id-{} {} {}", itemOwnerId, request.getMethod(), request.getRequestURI());
        return bookingClient.patchBooking(itemOwnerId, bookingId, approved);
    }

}