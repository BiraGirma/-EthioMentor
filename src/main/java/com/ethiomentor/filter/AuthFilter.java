package com.ethiomentor.filter;

import com.ethiomentor.dao.UserDAO;
import com.ethiomentor.model.User;
import com.ethiomentor.util.JsonUtil;
import com.ethiomentor.util.SessionUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Server-side access control for every request:
 *  - public pages and static assets are open;
 *  - everything else needs a logged-in, still-active user;
 *  - /admin needs role admin, /mentor-dashboard needs role mentor;
 *  - the page-style JSPs that need servlet-prepared data are redirected to their servlet.
 * Registered in web.xml (order matters: security headers, auth, csrf).
 */
public class AuthFilter implements Filter {

    private static final long ACTIVE_RECHECK_MS = 60_000L;
    private static final String ACTIVE_CHECKED_AT = "activeCheckedAt";

    /** Direct JSP URL (lower-case) -> servlet URL that prepares its data. */
    private static final Map<String, String> JSP_TO_SERVLET = Map.of(
            "/admin.jsp", "/admin",
            "/dashboard.jsp", "/dashboard",
            "/mentordashboard.jsp", "/mentor-dashboard",
            "/studygroups.jsp", "/studygroups");

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = pathOf(req);
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(SessionUtil.USER);
        if (user == null) {
            deny(req, res);
            return;
        }

        if (!stillActive(session, user)) {
            session.invalidate();
            deny(req, res);
            return;
        }

        String required = requiredRole(path);
        if (required != null && !required.equalsIgnoreCase(user.getRole())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        String servlet = JSP_TO_SERVLET.get(path);
        if (servlet != null && "GET".equalsIgnoreCase(req.getMethod())) {
            res.sendRedirect(req.getContextPath() + servlet);
            return;
        }

        chain.doFilter(request, response);
    }

    /** Decoded, normalised, parameter-free path below the context root, lower-cased. */
    static String pathOf(HttpServletRequest req) {
        String p = req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo());
        return p.toLowerCase();
    }

    public static boolean isPublic(String path) {
        return path.equals("/") || path.isEmpty()
                || path.equals("/index.jsp")
                || path.equals("/login") || path.equals("/login.jsp")
                || path.equals("/register") || path.equals("/register.jsp")
                || path.equals("/logout")
                || path.equals("/error.jsp")
                || path.startsWith("/css/") || path.startsWith("/js/");
    }

    public static String requiredRole(String path) {
        if (path.equals("/admin") || path.equals("/admin.jsp")) return "admin";
        if (path.equals("/mentor-dashboard") || path.equals("/mentordashboard.jsp")) return "mentor";
        return null;
    }

    public static boolean wantsJson(String path) {
        return path.startsWith("/api/") || path.startsWith("/loadmessages")
                || path.startsWith("/studygroups/") || path.startsWith("/mentormatching");
    }

    private void deny(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (wantsJson(pathOf(req))) {
            JsonUtil.error(res, HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
        } else {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    /** Re-checks (at most once a minute) that an admin has not deactivated the account. */
    private boolean stillActive(HttpSession session, User user) {
        long now = System.currentTimeMillis();
        Long last = (Long) session.getAttribute(ACTIVE_CHECKED_AT);
        if (last != null && now - last < ACTIVE_RECHECK_MS) return true;
        try {
            if (!userDAO.isActive(user.getId())) return false;
            session.setAttribute(ACTIVE_CHECKED_AT, now);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // database hiccup: do not lock everyone out
        }
    }
}
