package alexander.sergeev.stuff_sharing_app.booking.dto;

import alexander.sergeev.stuff_sharing_app.validation.BookingDateTimeValidation;
import alexander.sergeev.stuff_sharing_app.validation.ValidationMarker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@BookingDateTimeValidation(groups = ValidationMarker.OnCreate.class)
public class IncomingBookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

}