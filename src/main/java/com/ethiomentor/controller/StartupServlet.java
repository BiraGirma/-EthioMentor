package com.ethiomentor.controller;

import com.ethiomentor.config.DBInit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/** Initialises the database schema when the web application starts. */
@WebServlet(urlPatterns = "/StartupServlet", loadOnStartup = 1)
public class StartupServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        DBInit.init();
    }
}
