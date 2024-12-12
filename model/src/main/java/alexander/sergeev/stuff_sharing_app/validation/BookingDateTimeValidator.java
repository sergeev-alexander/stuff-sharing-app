package alexander.sergeev.stuff_sharing_app.validation;

import alexander.sergeev.stuff_sharing_app.booking.dto.IncomingBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingDateTimeValidator implements ConstraintValidator<BookingDateTimeValidation, IncomingBookingDto> {

    @Override
    public boolean isValid(IncomingBookingDto incomingBookingDto, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if (null == incomingBookingDto.getStart()) {
            context.buildConstraintViolationWithTemplate("Creating booking start field is null!")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }
        if (null == incomingBookingDto.getEnd()) {
            context.buildConstraintViolationWithTemplate("Creating booking end field is null!")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            return false;
        }
        if (incomingBookingDto.getStart().isBefore(LocalDateTime.now())) {
            context.buildConstraintViolationWithTemplate("Creating booking start is in past!")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }
        if (incomingBookingDto.getEnd().isBefore(LocalDateTime.now())) {
            context.buildConstraintViolationWithTemplate("Creating booking end is in past!")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            return false;
        }
        if (incomingBookingDto.getStart().isAfter(incomingBookingDto.getEnd())) {
            context.buildConstraintViolationWithTemplate("Creating booking start is before end!")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }
        if (incomingBookingDto.getStart().equals(incomingBookingDto.getEnd())) {
            context.buildConstraintViolationWithTemplate("Creating booking start equal end!")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}