package com.ethiomentor.util;

import java.util.regex.Pattern;

/** Small server-side validation and output-escaping helpers. */
public final class InputUtil {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.-]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private InputUtil() {}

    /** Trims; returns null for null input. */
    public static String trim(String s) {
        return s == null ? null : s.trim();
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidUsername(String s) {
        return s != null && USERNAME.matcher(s).matches();
    }

    public static boolean isValidEmail(String s) {
        return s != null && s.length() <= 100 && EMAIL.matcher(s).matches();
    }

    public static boolean isValidPassword(String s) {
        return s != null && s.length() >= 8 && s.length() <= 128;
    }

    public static boolean lengthBetween(String s, int min, int max) {
        return s != null && s.length() >= min && s.length() <= max;
    }

    /** Parses a positive int, or returns -1. */
    public static int parseId(String s) {
        if (s == null) return -1;
        try {
            int v = Integer.parseInt(s.trim());
            return v > 0 ? v : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** HTML-escape for use in scriptlet output (JSTL pages should use c:out). */
    public static String escapeHtml(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
