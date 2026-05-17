package com.erdouglass.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {
  
  String message() default "Date must be between {min} and {max}";
  
  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String min();

  String max();  
}
