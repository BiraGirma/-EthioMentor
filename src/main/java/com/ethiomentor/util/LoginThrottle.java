package com.ethiomentor.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory brute-force limiter: at most MAX_FAILURES failed logins per key
 * within WINDOW_MS. State is per-JVM and lost on restart (adequate for a
 * single Tomcat instance).
 */
public final class LoginThrottle {

    private static final int MAX_FAILURES = 5;
    private static final long WINDOW_MS = 15 * 60 * 1000L;
    private static final int MAX_KEYS = 10_000;

    private static final ConcurrentHashMap<String, Deque<Long>> FAILURES = new ConcurrentHashMap<>();

    private LoginThrottle() {}

    public static boolean isBlocked(String key) {
        return isBlocked(key, System.currentTimeMillis());
    }

    public static boolean isBlocked(String key, long now) {
        Deque<Long> d = FAILURES.get(key);
        if (d == null) return false;
        synchronized (d) {
            prune(d, now);
            return d.size() >= MAX_FAILURES;
        }
    }

    public static void recordFailure(String key) {
        recordFailure(key, System.currentTimeMillis());
    }

    public static void recordFailure(String key, long now) {
        if (FAILURES.size() > MAX_KEYS) FAILURES.clear(); // crude memory bound
        Deque<Long> d = FAILURES.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (d) {
            prune(d, now);
            d.addLast(now);
        }
    }

    public static void reset(String key) {
        FAILURES.remove(key);
    }

    public static void clearAll() {
        FAILURES.clear();
    }

    private static void prune(Deque<Long> d, long now) {
        while (!d.isEmpty() && now - d.peekFirst() > WINDOW_MS) d.pollFirst();
    }
}
