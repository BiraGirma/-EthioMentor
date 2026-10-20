package com.ethiomentor.test;

/** Tiny dependency-free test helper (no JUnit needed on the developer machine). */
public final class Check {

    private static int passed, failed;

    private Check() {}

    public interface Body { void run() throws Exception; }

    public static void run(String name, Body body) {
        try {
            body.run();
            passed++;
            System.out.println("  PASS  " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("  FAIL  " + name + "  -> " + t);
        }
    }

    public static void isTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void isFalse(boolean cond, String msg) {
        if (cond) throw new AssertionError(msg);
    }

    public static void eq(Object expected, Object actual, String msg) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(msg + " (expected <" + expected + ">, got <" + actual + ">)");
        }
    }

    /** Prints the summary and exits non-zero if anything failed. */
    public static void finish(String suite) {
        System.out.println(suite + ": " + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }
}
