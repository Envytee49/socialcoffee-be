package com.example.socialcoffee.utils;

import com.example.socialcoffee.constants.DateTimeFormat;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static LocalDate convertStringToLocalDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormat.MMDDYYYY);
        return LocalDate.parse(dateStr, formatter);
    }
}
