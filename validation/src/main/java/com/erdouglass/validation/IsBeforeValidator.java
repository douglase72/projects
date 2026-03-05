package com.erdouglass.validation;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsBeforeValidator implements ConstraintValidator<IsBefore, LocalDate> {  
  private String validDate;
  
  @Override
  public void initialize(IsBefore constraintAnnotation) {
      validDate = constraintAnnotation.value();
  }  

  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    if (Objects.isNull(value)) {
      return true;
    }
    return value.isBefore(LocalDate.parse(validDate));
  }

}