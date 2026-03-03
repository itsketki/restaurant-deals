package com.eatclub.restaurant.deal.util;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilTest {


    @Test
    void testTimeParsing_24HourFormat() {
        String time = "15:59";
        LocalTime expected = LocalTime.of(15, 59 );
        LocalTime actualTime = TimeUtil.parseTime(time);
        assertEquals(expected, actualTime);
    }

    @Test
    void testTimeParsing_12HourFormatLowerCase() {
        String time = "05:40pm";
        LocalTime expected = LocalTime.of(17, 40);
        LocalTime actualTime = TimeUtil.parseTime(time);
        assertEquals(expected, actualTime);
    }

    @Test
    void testTimeParsing_12HourFormatUpperCase() {
        String time = "10:30AM";
        LocalTime expected = LocalTime.of(10, 30);
        LocalTime actualTime = TimeUtil.parseTime(time);
        assertEquals(expected, actualTime);
    }

    @Test
    void testTimeParsing_BlankInput() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> TimeUtil.parseTime(" "));

        assertEquals("Time string cannot be null or empty",
                exception.getMessage());
    }

    @Test
    void testTimeParsing_NullInput() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> TimeUtil.parseTime(null));

        assertEquals("Time string cannot be null or empty",
                exception.getMessage());
    }

}