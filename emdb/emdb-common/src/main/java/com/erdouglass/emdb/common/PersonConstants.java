package com.erdouglass.emdb.common;

public final class PersonConstants {
  
  /// The maximum length for a person's `biography`.
  public static final int BIOGRAPHY_MAX_LENGTH = 3900;
  
  /// The maximum length for a person's `birth_place`.
  public static final int BIRTH_PLACE_MAX_LENGTH = 120;
  
  public static final int GENDER_MAX_LENGTH  = 10;

  /// The maximum valid date for a person.
  public static final String MAX_DATE = "2100-01-01";
    
  /// The minimum valid date allowed for `birthDate` and `deathDate`.
  public static final String MIN_DATE = "1699-12-31";
    
  /// The maximum length for a persons name.
  public static final int NAME_MAX_LENGTH = 80;
    
  /// The maximum length for a person's `profile` image path.
  public static final int PROFILE_MAX_LENGTH = 80;
  
  /// The minimum length for a person's `profile` image path.
  public static final int PROFILE_MIN_LENGTH = 1;
    
  private PersonConstants() { }
    
}
