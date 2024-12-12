package alexander.sergeev.stuff_sharing_app.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class BookingStateValidator implements ConstraintValidator<BookingStateValidation, String> {

    @Override
    public boolean isValid(String bookingStateString, ConstraintValidatorContext context) {
        if (Arrays.stream(BookingState.values()).noneMatch((value) -> String.valueOf(value).equals(bookingStateString))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Unknown state: UNSUPPORTED_STATUS")
                    .addPropertyNode("state")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
