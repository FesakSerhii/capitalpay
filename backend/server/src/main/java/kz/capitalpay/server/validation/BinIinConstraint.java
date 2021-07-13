package kz.capitalpay.server.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BinIinValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BinIinConstraint {
    String message() default "The bin or iin is not valid!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
