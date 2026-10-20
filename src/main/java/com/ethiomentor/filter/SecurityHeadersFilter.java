package com.ethiomentor.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Adds basic hardening headers; stops browsers caching logged-in pages (back button after logout). */
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("X-Frame-Options", "SAMEORIGIN");
        res.setHeader("Referrer-Policy", "same-origin");

        String path = AuthFilter.pathOf(req);
        if (!path.startsWith("/css/") && !path.startsWith("/js/")) {
            res.setHeader("Cache-Control", "no-store");
        }
        chain.doFilter(request, response);
    }
}
