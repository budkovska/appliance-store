package com.epam.rd.autocode.assessment.appliances.aspect;

public final class LogSanitizer {

    private LogSanitizer() {
    }

    public static String clean(Object value, int maxLength) {
        if (value == null) {
            return "null";
        }
        String text = String.valueOf(value).replaceAll("[\\r\\n\\t]+", " ");
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}