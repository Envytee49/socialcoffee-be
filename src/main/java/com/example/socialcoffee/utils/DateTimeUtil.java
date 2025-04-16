package com.example.socialcoffee.utils;

import com.example.socialcoffee.constants.DateTimeFormat;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DateTimeUtil {
    public static LocalDate convertStringToLocalDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat.MMDDYYYY);
        return LocalDate.parse(dateStr, formatter);
    }

    public static String covertLocalDateToString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        return localDate.format(formatter);
    }

    public static String getTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt.isAfter(now)) {
            return "just now";
        }

        long seconds = ChronoUnit.SECONDS.between(createdAt, now);
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        if (minutes < 60) {
            return minutes + "m";
        }

        long hours = ChronoUnit.HOURS.between(createdAt, now);
        if (hours < 24) {
            return hours + "h";
        }

        long days = ChronoUnit.DAYS.between(createdAt, now);
        if (days < 30) {
            return days + "d";
        }

        long months = ChronoUnit.MONTHS.between(createdAt, now);
        if (months < 12) {
            return months + "mo";
        }

        long years = ChronoUnit.YEARS.between(createdAt, now);
        return years + "y";
    }
}
