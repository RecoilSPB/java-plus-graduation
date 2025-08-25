package ru.yandex.practicum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.ConstraintComposition;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hibernate.validator.constraints.CompositionType.OR;

@Null
@NotBlank
@Retention(RUNTIME)
@ReportAsSingleViolation
@ConstraintComposition(OR)
@Constraint(validatedBy = {})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
public @interface NullOrNotBlank {
    String message() default "must be null or not blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}