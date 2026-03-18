package com.hostel.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class Validation {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private Validation() {
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number.");
        }
        return value;
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    public static String requireMinLength(String value, String fieldName, int minLength) {
        requireNonBlank(value, fieldName);
        if (value.trim().length() < minLength) {
            throw new IllegalArgumentException(fieldName + " must be at least " + minLength + " characters.");
        }
        return value.trim();
    }

    public static String normalizeDate(String date) {
        String trimmed = requireNonBlank(date, "Date");
        try {
            LocalDate parsed = LocalDate.parse(trimmed, DATE_FORMAT);
            return parsed.toString();
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid date. Use yyyy-MM-dd.");
        }
    }
}
