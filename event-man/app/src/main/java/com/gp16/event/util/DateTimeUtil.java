package com.gp16.event.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a");
    
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(formatter);
    }
} 