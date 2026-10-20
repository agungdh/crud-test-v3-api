package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * API-level gender type. Persisted as the {@code is_male} boolean column;
 * the conversion lives in {@link com.example.demo.mapper.PegawaiMapper}.
 */
public enum Gender {

    MALE,
    FEMALE;

    @JsonValue
    public String value() {
        return name().toLowerCase(Locale.ROOT);
    }

    @JsonCreator
    public static Gender fromValue(String value) {
        return value == null ? null : valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
