package com.eatclub.restaurant.deal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class TimeUtil {
    private static final Logger logger = LoggerFactory.getLogger(TimeUtil.class);

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH)
    };

    private TimeUtil() { }

    public static LocalTime parseTime(String time) {
        if (time == null || time.isBlank()) {
            throw new IllegalArgumentException("Time string cannot be null or empty");
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalTime.parse(time.trim().toUpperCase(), formatter);
            } catch (DateTimeParseException ignored) { //try next
            }
        }

        throw new IllegalArgumentException("Invalid time format. Expected 24-hour HH:mm or 12-hour h:mma");

    }
}
