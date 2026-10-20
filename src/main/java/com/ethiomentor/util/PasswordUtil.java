package com.ethiomentor.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Password hashing using salted PBKDF2-HMAC-SHA256 (JDK built-in, no extra jar).
 *
 * Stored format:  pbkdf2_sha256$<iterations>$<salt b64>$<hash b64>
 *
 * Legacy support: earlier versions stored an unsalted SHA-256 hex digest
 * (64 hex chars). Those are still accepted by verify() so existing accounts
 * keep working, and needsUpgrade() tells the caller to upgrade them on login.
 */
public final class PasswordUtil {

    private static final String PREFIX = "pbkdf2_sha256";
    private static final int ITERATIONS = 600_000;   // OWASP guidance for PBKDF2-HMAC-SHA256
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] dk = pbkdf2(password, salt, ITERATIONS);
        Base64.Encoder enc = Base64.getEncoder();
        return PREFIX + "$" + ITERATIONS + "$" + enc.encodeToString(salt) + "$" + enc.encodeToString(dk);
    }

    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) return false;

        if (isLegacy(stored)) {
            return MessageDigest.isEqual(
                    sha256Hex(password).getBytes(StandardCharsets.US_ASCII),
                    stored.toLowerCase().getBytes(StandardCharsets.US_ASCII));
        }

        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) return false;
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(password, salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {   // bad number or bad base64
            return false;
        }
    }

    /** True for legacy unsalted hashes and for hashes made with fewer iterations than today's. */
    public static boolean needsUpgrade(String stored) {
        if (stored == null) return true;
        if (isLegacy(stored)) return true;
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) return true;
        try {
            return Integer.parseInt(parts[1]) < ITERATIONS;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static boolean isLegacy(String stored) {
        return stored != null && stored.matches("[0-9a-fA-F]{64}");
    }

    /** Unsalted SHA-256 hex digest. Only for recognising legacy hashes; never use for new passwords. */
    public static String sha256Hex(String password) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 unavailable", e);
        } finally {
            spec.clearPassword();
        }
    }
}
