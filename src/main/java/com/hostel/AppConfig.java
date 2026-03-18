package com.hostel;

public final class AppConfig {

    private AppConfig() {
    }

    public static String getMongoUri() {
        return getEnvOrProp("MONGODB_URI", "mongodb.uri", "mongodb://127.0.0.1:27017");
    }

    public static String getDatabaseName() {
        return getEnvOrProp("MONGODB_DB", "mongodb.db", "hostelDB");
    }

    public static String getAdminUser() {
        return getEnvOrProp("ADMIN_USER", "admin.user", "admin");
    }

    public static String getAdminPass() {
        return getEnvOrProp("ADMIN_PASS", "admin.pass", "admin123");
    }

    public static boolean isDefaultAdminCredentials() {
        return "admin".equals(getAdminUser()) && "admin123".equals(getAdminPass());
    }

    public static java.util.List<String> getBookingSlots() {
        String raw = getEnvOrProp("BOOKING_SLOTS", "booking.slots",
                "08:00-10:00,10:00-12:00,12:00-14:00,14:00-16:00,16:00-18:00");

        java.util.LinkedHashSet<String> slots = new java.util.LinkedHashSet<>();
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                slots.add(trimmed);
            }
        }

        if (slots.isEmpty()) {
            slots.add("08:00-10:00");
            slots.add("10:00-12:00");
            slots.add("12:00-14:00");
            slots.add("14:00-16:00");
            slots.add("16:00-18:00");
        }

        return java.util.List.copyOf(slots);
    }

    private static String getEnvOrProp(String envKey, String propKey, String defaultValue) {
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) {
            value = System.getProperty(propKey);
        }
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }
}
