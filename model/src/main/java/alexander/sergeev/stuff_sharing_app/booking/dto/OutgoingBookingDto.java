package alexander.sergeev.stuff_sharing_app.booking.dto;

import alexander.sergeev.stuff_sharing_app.booking.model.BookingStatus;
import alexander.sergeev.stuff_sharing_app.item.model.Item;
import alexander.sergeev.stuff_sharing_app.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutgoingBookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private User booker;

    private Item item;

    private BookingStatus status;

}