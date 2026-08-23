package com.intellitransit.util;

import java.time.LocalTime;

public class GtfsTimeUtil {

    public static class GtfsTimeResult {
        private final LocalTime localTime;
        private final int dayOffset;

        public GtfsTimeResult(LocalTime localTime, int dayOffset) {
            this.localTime = localTime;
            this.dayOffset = dayOffset;
        }

        public LocalTime getLocalTime() { return localTime; }
        public int getDayOffset() { return dayOffset; }
    }

    public static GtfsTimeResult parseGtfsTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("GTFS time string cannot be null or empty");
        }

        String[] parts = timeStr.trim().split(":");
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("Invalid GTFS time format: " + timeStr);
        }

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;

        int dayOffset = hours / 24;
        int normalizedHours = hours % 24;

        LocalTime localTime = LocalTime.of(normalizedHours, minutes, seconds);
        return new GtfsTimeResult(localTime, dayOffset);
    }
}
