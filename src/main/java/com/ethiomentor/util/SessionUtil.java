package com.ethiomentor.util;

import com.ethiomentor.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

/** Session helpers shared by servlets and filters. */
public final class SessionUtil {

    public static final String USER = "user";
    public static final String USER_ID = "userId";
    public static final String ROLE = "role";
    public static final String CSRF = "csrfToken";

    private static final SecureRandom RANDOM = new SecureRandom();

    private SessionUtil() {}

    /** The logged-in user, or null. Never creates a session. */
    public static User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute(USER);
    }

    /**
     * Starts an authenticated session. An existing session id is rotated
     * (session-fixation protection) and a fresh CSRF token is issued.
     */
    public static void login(HttpServletRequest req, User user) {
        user.setPassword(null); // never keep the hash in the session
        HttpSession s = req.getSession(false);
        if (s != null) {
            req.changeSessionId();
        } else {
            s = req.getSession(true);
        }
        s.setAttribute(USER, user);
        s.setAttribute(USER_ID, user.getId());
        s.setAttribute(ROLE, user.getRole());
        byte[] t = new byte[32];
        RANDOM.nextBytes(t);
        s.setAttribute(CSRF, Base64.getUrlEncoder().withoutPadding().encodeToString(t));
    }

    /** Where each role lands after login. */
    public static String homeFor(User user) {
        String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
        return switch (role) {
            case "admin" -> "/admin";
            case "mentor" -> "/mentor-dashboard";
            default -> "/dashboard";
        };
    }
}
