package com.ethiomentor.filter;

import com.ethiomentor.util.SessionUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;

/**
 * Rejects state-changing requests (POST/PUT/PATCH/DELETE) from logged-in users
 * unless they carry the session's CSRF token, either as the X-CSRF-Token header
 * (fetch/AJAX) or a "_csrf" form field. Login and registration are exempt (there
 * is no session yet); the session cookie is also SameSite=Lax (META-INF/context.xml).
 */
public class CsrfFilter implements Filter {

    private static final Set<String> SAFE = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (SAFE.contains(req.getMethod().toUpperCase()) || exempt(AuthFilter.pathOf(req))) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        String expected = session == null ? null : (String) session.getAttribute(SessionUtil.CSRF);
        String supplied = req.getHeader("X-CSRF-Token");
        if (supplied == null) supplied = req.getParameter("_csrf");

        if (expected == null || supplied == null || !constantTimeEquals(expected, supplied)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing or invalid CSRF token");
            return;
        }
        chain.doFilter(request, response);
    }

    public static boolean exempt(String path) {
        return path.equals("/login") || path.equals("/register") || path.equals("/logout");
    }

    public static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
