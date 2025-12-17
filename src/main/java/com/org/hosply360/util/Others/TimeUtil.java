package com.org.hosply360.util.Others;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private TimeUtil() {}

    private static final DateTimeFormatter FORMATTER_HHMM = DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter FORMATTER_HHMMSS = DateTimeFormatter.ofPattern("HH:mm:ss");


    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, FORMATTER_HHMM);
    }

    public static String formatTime(LocalTime time) {
        return time.format(FORMATTER_HHMMSS);
    }

    public static String formatTime2(LocalTime time) {
        return time.format(FORMATTER_HHMM);
    }

    public static LocalDateTime toDateTime(LocalDate date, String timeStr) {
        return LocalDateTime.of(date, parseTime(timeStr));
    }

    public static String formatDate(LocalDateTime dt) {
        return (dt != null)
                ? dt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"))
                : "";
    }
}
