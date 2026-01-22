package com.ethiomentor.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonUtil {

    public static void send(HttpServletResponse resp, int status, String json) {
        try {
            resp.setStatus(status);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            PrintWriter out = resp.getWriter();
            out.print(json);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
