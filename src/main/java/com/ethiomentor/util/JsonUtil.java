package com.ethiomentor.util;

import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public final class JsonUtil {

    private JsonUtil() {}

    /** Sends a JSON string with a given HTTP status code. */
    public static void send(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

    public static void send(HttpServletResponse resp, int status, JSONObject json) throws IOException {
        send(resp, status, json.toString());
    }

    public static void error(HttpServletResponse resp, int status, String message) throws IOException {
        send(resp, status, new JSONObject().put("error", message));
    }
}
