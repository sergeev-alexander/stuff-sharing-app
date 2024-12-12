package alexander.sergeev.stuff_sharing_app.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = BookingDateTimeValidator.class)
@Documented
public @interface BookingDateTimeValidation {

    String message() default "Booking validation failed!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}