package com.ethiomentor.controller;

<<<<<<< HEAD
import com.ethiomentor.util.DBConnection;

=======
import com.ethiomentor.config.DBConfig;
>>>>>>> 8fb5b46 (finilized)
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        req.getRequestDispatcher("/dashboard.jsp").forward(req, res);
    }
}
