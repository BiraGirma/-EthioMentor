package com.ethiomentor.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonUtil {

    /** 
     * Sends a JSON string with a given HTTP status code.
     */
    public static void send(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
            out.flush();
        }
    }
}
