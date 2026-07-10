package com.erdouglass.emdb.media.domain.movie;

import java.util.UUID;

import com.erdouglass.emdb.media.Gender;
import com.erdouglass.emdb.media.domain.shared.Credit.CreditType;

public record MovieCreditProjection(
    CreditType type, 
    String role, 
    Integer creditOrder,
    Long personId, 
    String name, 
    Gender gender, 
    UUID profile) {}
