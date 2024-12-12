package alexander.sergeev.stuff_sharing_app.booking;

import alexander.sergeev.stuff_sharing_app.booking.dto.IncomingBookingDto;
import alexander.sergeev.stuff_sharing_app.client.BaseClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${stuff_sharing_app_server_url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getAllUserBookings(Long userId,
                                                     String bookingStateString,
                                                     Integer from,
                                                     Integer size) {
        return get("?state={state}&from={from}&size={size}", userId,
                Map.of("state", bookingStateString, "from", from, "size", size));
    }

    public ResponseEntity<Object> getAllOwnerItemBookings(Long ownerId,
                                                          String bookingStateString,
                                                          Integer from,
                                                          Integer size) {
        return get("/owner?state={state}&from={from}&size={size}", ownerId,
                Map.of("state", bookingStateString, "from", from, "size", size));
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> postBooking(Long bookerId, IncomingBookingDto incomingBookingDto) {
        return post("", bookerId, incomingBookingDto);
    }

    public ResponseEntity<Object> patchBooking(Long itemOwnerId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved={approved}", itemOwnerId,
                Map.of("approved", approved), new IncomingBookingDto());
    }
}