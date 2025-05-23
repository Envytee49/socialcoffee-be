package com.example.socialcoffee.utils;

import com.example.socialcoffee.constants.DateTimeFormat;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

public class DateTimeUtil {
    public static LocalDate convertStringToLocalDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat.MMDDYYYY);
        return LocalDate.parse(dateStr,
                formatter);
    }

    public static String covertLocalDateToYYYYMMDDString(LocalDate localDate) {
        if (Objects.isNull(localDate)) return StringUtils.EMPTY;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat.YYYYMMDD_HYPHEN);
        return localDate.format(formatter);
    }

    public static LocalDate convertYYYYMMDDStrToLocalDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        try {
            return convertMMMDYYYStrToLocalDate(dateStr);
        } catch (Exception e) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat.YYYYMMDD_HYPHEN);
            return LocalDate.parse(dateStr,
                    formatter);
        }
    }

    private static LocalDate convertMMMDYYYStrToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy",
                Locale.ENGLISH);
        return LocalDate.parse(dateStr,
                formatter);
    }

    public static String convertMinuteToHour(Integer minute) {
        if (minute == null) return null;

        int hour = minute / 60;
        String period = hour < 12 ? "AM" : "PM";
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;

        return displayHour + " " + period;
    }

    public static String checkCurrentOpenStatus(Integer open,
                                                Integer close) {
        if (open == null || close == null) return null;

        LocalTime now = LocalTime.now();
        int currentMinutes = now.getHour() * 60 + now.getMinute();

        // Rule: Always closed before 7:00 AM
        if (currentMinutes < 420) { // 7 * 60 = 420
            return "Closed";
        }

        if (open <= close) {
            // Same-day closing (e.g., 9 AM - 5 PM)
            if (open <= currentMinutes && currentMinutes < close) {
                return "Open";
            }
        } else {
            // Overnight hours (e.g., 8 PM - 2 AM)
            if (currentMinutes >= open || currentMinutes < close) {
                return "Open";
            }
        }

        return "Closed";
    }

    public static String getTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt.isAfter(now)) {
            return "just now";
        }

        long seconds = ChronoUnit.SECONDS.between(createdAt,
                now);
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = ChronoUnit.MINUTES.between(createdAt,
                now);
        if (minutes < 60) {
            return minutes + "m";
        }

        long hours = ChronoUnit.HOURS.between(createdAt,
                now);
        if (hours < 24) {
            return hours + "h";
        }

        long days = ChronoUnit.DAYS.between(createdAt,
                now);
        if (days < 30) {
            return days + "d";
        }

        long months = ChronoUnit.MONTHS.between(createdAt,
                now);
        if (months < 12) {
            return months + "mo";
        }

        long years = ChronoUnit.YEARS.between(createdAt,
                now);
        return years + "y";
    }

    public static String covertLocalDateToString(LocalDateTime createdAt) {
        if (Objects.isNull(createdAt)) return StringUtils.EMPTY;
        return covertLocalDateToString(createdAt.toLocalDate());
    }

    public static String covertLocalDateToString(LocalDate localDate) {
        if (Objects.isNull(localDate)) return StringUtils.EMPTY;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy",
                Locale.ENGLISH);
        return localDate.format(formatter);
    }

    public static String convertIntegerToString(Integer minutes) {
        if (Objects.isNull(minutes)) return StringUtils.EMPTY;
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        // Format hours and minutes to ensure two digits
        return String.format("%02d:%02d",
                hours,
                remainingMinutes);
    }
}
