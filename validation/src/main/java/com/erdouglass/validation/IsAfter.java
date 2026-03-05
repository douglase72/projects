package com.erdouglass.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RUNTIME)
@Constraint(validatedBy = { IsAfterValidator.class })
@Target({FIELD})
public @interface IsAfter {
  
  String message() default "must be after {value}";
  
  String value();
  
  Class<?>[] groups() default { };
  
  Class<? extends Payload>[] payload() default { };
  
}