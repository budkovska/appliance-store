package com.epam.rd.autocode.assessment.appliances.repository.specification;

import java.util.Locale;

public final class SearchUtils {

    public static final char ESCAPE = '!';

    private SearchUtils() {
    }

    public static String likePattern(String text) {
        String value = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
        String escaped = value
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escaped + "%";
    }
}