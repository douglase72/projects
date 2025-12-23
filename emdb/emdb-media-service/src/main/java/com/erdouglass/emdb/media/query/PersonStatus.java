package com.erdouglass.emdb.media.query;

import com.erdouglass.emdb.media.entity.Person;

public record PersonStatus(Person person, StatusCode statusCode) {

}
