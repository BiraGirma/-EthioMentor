package com.ethiomentor.test;

import com.ethiomentor.controller.ChatWebSocket;
import com.ethiomentor.filter.AuthFilter;
import com.ethiomentor.filter.CsrfFilter;
import com.ethiomentor.model.User;
import com.ethiomentor.service.UserService;
import com.ethiomentor.util.InputUtil;
import com.ethiomentor.util.LoginThrottle;
import com.ethiomentor.util.PasswordUtil;

/** Pure-logic tests: need no database and no servlet container. */
public class UnitTests {

    public static void main(String[] args) {
        System.out.println("UnitTests");

        Check.run("PasswordUtil: hash/verify round trip", () -> {
            String h = PasswordUtil.hash("correct horse");
            Check.isTrue(h.startsWith("pbkdf2_sha256$"), "format");
            Check.isTrue(PasswordUtil.verify("correct horse", h), "right password");
            Check.isFalse(PasswordUtil.verify("wrong", h), "wrong password");
            Check.isFalse(PasswordUtil.needsUpgrade(h), "fresh hash needs no upgrade");
        });
        Check.run("PasswordUtil: salted (same password, different hash)", () ->
            Check.isFalse(PasswordUtil.hash("x-password").equals(PasswordUtil.hash("x-password")), "salt"));
        Check.run("PasswordUtil: legacy SHA-256 verifies and is flagged for upgrade", () -> {
            String legacy = PasswordUtil.sha256Hex("admin123");
            Check.eq("240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", legacy, "known digest");
            Check.isTrue(PasswordUtil.verify("admin123", legacy), "legacy verify");
            Check.isFalse(PasswordUtil.verify("admin124", legacy), "legacy wrong");
            Check.isTrue(PasswordUtil.needsUpgrade(legacy), "legacy upgrade flag");
        });
        Check.run("PasswordUtil: garbage / null never verifies", () -> {
            Check.isFalse(PasswordUtil.verify("a", null), "null stored");
            Check.isFalse(PasswordUtil.verify(null, "abc"), "null password");
            Check.isFalse(PasswordUtil.verify("a", "pbkdf2_sha256$x$y"), "malformed");
            Check.isFalse(PasswordUtil.verify("a", "pbkdf2_sha256$1$!!$!!"), "bad base64");
            Check.isFalse(PasswordUtil.verify("a", ""), "empty");
        });
        Check.run("PasswordUtil: non-ASCII password (Amharic)", () -> {
            String h = PasswordUtil.hash("የይለፍቃል123");
            Check.isTrue(PasswordUtil.verify("የይለፍቃል123", h), "unicode");
        });

        Check.run("InputUtil: validators", () -> {
            Check.isTrue(InputUtil.isValidUsername("abebe_k-1.x"), "ok username");
            Check.isFalse(InputUtil.isValidUsername("ab"), "too short");
            Check.isFalse(InputUtil.isValidUsername("a b c"), "space");
            Check.isFalse(InputUtil.isValidUsername("<script>"), "markup");
            Check.isTrue(InputUtil.isValidEmail("user@university.edu.et"), "ok email");
            Check.isFalse(InputUtil.isValidEmail("nope"), "bad email");
            Check.isTrue(InputUtil.isValidPassword("12345678"), "8 chars");
            Check.isFalse(InputUtil.isValidPassword("1234567"), "7 chars");
            Check.eq(5, InputUtil.parseId(" 5 "), "parseId ok");
            Check.eq(-1, InputUtil.parseId("abc"), "parseId junk");
            Check.eq(-1, InputUtil.parseId("0"), "parseId zero");
            Check.eq(-1, InputUtil.parseId(null), "parseId null");
        });
        Check.run("InputUtil: escapeHtml", () -> {
            Check.eq("&lt;script&gt;alert(&quot;x&quot;)&lt;/script&gt; &amp; &#39;", InputUtil.escapeHtml("<script>alert(\"x\")</script> & '"), "escape");
            Check.eq("", InputUtil.escapeHtml(null), "null");
        });

        Check.run("UserService.validateRegistration", () -> {
            User u = new User();
            u.setUsername("abebe"); u.setFullName("Abebe Bikila"); u.setEmail("a@b.co"); u.setUniversity("AAU");
            Check.eq(null, UserService.validateRegistration(u, "longenough"), "valid");
            Check.isTrue(UserService.validateRegistration(u, "short") != null, "short password");
            u.setEmail("bad");
            Check.isTrue(UserService.validateRegistration(u, "longenough") != null, "bad email");
        });
        Check.run("UserService.cleanExpertise", () -> {
            Check.eq("java,sql", UserService.cleanExpertise(" java , sql,, "), "trim/empty");
            Check.isTrue(UserService.cleanExpertise("x".repeat(300)).length() <= 255, "cap");
        });

        Check.run("LoginThrottle: blocks after 5 failures, window expires, reset works", () -> {
            LoginThrottle.clearAll();
            long t0 = 1_000_000L;
            for (int i = 0; i < 4; i++) LoginThrottle.recordFailure("k", t0 + i);
            Check.isFalse(LoginThrottle.isBlocked("k", t0 + 10), "4 failures: not blocked");
            LoginThrottle.recordFailure("k", t0 + 5);
            Check.isTrue(LoginThrottle.isBlocked("k", t0 + 10), "5 failures: blocked");
            Check.isFalse(LoginThrottle.isBlocked("k", t0 + 16 * 60 * 1000L), "window expired");
            Check.isFalse(LoginThrottle.isBlocked("other", t0), "other key unaffected");
            LoginThrottle.reset("k");
            Check.isFalse(LoginThrottle.isBlocked("k", t0 + 10), "reset");
        });

        Check.run("AuthFilter: public paths", () -> {
            for (String p : new String[]{"/", "/index.jsp", "/login", "/login.jsp", "/register", "/register.jsp",
                                         "/logout", "/error.jsp", "/css/global.css", "/js/chat.js"})
                Check.isTrue(AuthFilter.isPublic(p), p + " should be public");
            for (String p : new String[]{"/admin", "/admin.jsp", "/dashboard", "/chat.jsp", "/loadmessages",
                                         "/api/user", "/studygroups", "/studygroups/create", "/mentormatching",
                                         "/cssx/evil", "/ws/chat", "/startupservlet", "/mentordashboard.jsp"})
                Check.isFalse(AuthFilter.isPublic(p), p + " must require login");
        });
        Check.run("AuthFilter: role rules", () -> {
            Check.eq("admin", AuthFilter.requiredRole("/admin"), "/admin");
            Check.eq("admin", AuthFilter.requiredRole("/admin.jsp"), "/admin.jsp");
            Check.eq("mentor", AuthFilter.requiredRole("/mentor-dashboard"), "/mentor-dashboard");
            Check.eq("mentor", AuthFilter.requiredRole("/mentordashboard.jsp"), "/mentorDashboard.jsp");
            Check.eq(null, AuthFilter.requiredRole("/dashboard"), "/dashboard");
        });
        Check.run("AuthFilter: JSON vs redirect for anonymous callers", () -> {
            Check.isTrue(AuthFilter.wantsJson("/api/user"), "api");
            Check.isTrue(AuthFilter.wantsJson("/loadmessages"), "loadMessages");
            Check.isTrue(AuthFilter.wantsJson("/studygroups/join"), "join");
            Check.isFalse(AuthFilter.wantsJson("/admin"), "admin page redirects");
        });
        Check.run("CsrfFilter: exemptions and token compare", () -> {
            Check.isTrue(CsrfFilter.exempt("/login"), "login exempt");
            Check.isTrue(CsrfFilter.exempt("/register"), "register exempt");
            Check.isFalse(CsrfFilter.exempt("/admin"), "admin not exempt");
            Check.isFalse(CsrfFilter.exempt("/studygroups/create"), "create not exempt");
            Check.isTrue(CsrfFilter.constantTimeEquals("abc", "abc"), "equal");
            Check.isFalse(CsrfFilter.constantTimeEquals("abc", "abd"), "different");
            Check.isFalse(CsrfFilter.constantTimeEquals("abc", "abcd"), "length");
        });
        Check.run("ChatWebSocket: origin must match host", () -> {
            Check.isTrue(ChatWebSocket.originMatchesHost("http://localhost:8080", "localhost:8080"), "same");
            Check.isTrue(ChatWebSocket.originMatchesHost("https://example.org", "example.org"), "default port");
            Check.isTrue(ChatWebSocket.originMatchesHost(null, "localhost:8080"), "no Origin header (non-browser)");
            Check.isFalse(ChatWebSocket.originMatchesHost("http://evil.example", "localhost:8080"), "cross-site");
            Check.isFalse(ChatWebSocket.originMatchesHost("http://localhost:9999", "localhost:8080"), "other port");
            Check.isFalse(ChatWebSocket.originMatchesHost("not a uri", "localhost:8080"), "garbage origin");
        });

        Check.finish("UnitTests");
    }
}
