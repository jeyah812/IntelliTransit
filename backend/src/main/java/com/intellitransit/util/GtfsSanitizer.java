package com.intellitransit.util;

import java.util.regex.Pattern;

public class GtfsSanitizer {

    private static final Pattern MALICIOUS_PATTERN = Pattern.compile(
            "(?i)(<script|javascript:|alert\\(|<html|<body|onload=|onerror=|<iframe|<img)"
    );

    public static boolean isSanitary(String value) {
        if (value == null) {
            return true;
        }
        return !MALICIOUS_PATTERN.matcher(value).find();
    }

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}
